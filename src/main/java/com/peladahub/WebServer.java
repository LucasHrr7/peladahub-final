package com.peladahub;

import com.peladahub.connection.ConnectionFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;

/** Servidor HTTP mínimo: hospeda a interface e salva/restaura o estado da rodada. */
public final class WebServer {
  private WebServer() {}
  public static void start(int port) throws IOException {
    prepareDatabase();
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/api/state", WebServer::state);
    server.createContext("/", WebServer::staticFile);
    server.setExecutor(null);
    server.start();
    System.out.println("PeladaHub disponível em http://localhost:" + port);
  }
  private static void prepareDatabase() {
    try (Connection c = new ConnectionFactory().getConnection(); Statement s = c.createStatement()) {
      s.executeUpdate("CREATE TABLE IF NOT EXISTS rodada_estado (id INT PRIMARY KEY, payload LONGTEXT NOT NULL, atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
    } catch (RuntimeException | SQLException e) { throw new IllegalStateException("Falha ao preparar o banco de dados", e); }
  }
  private static void state(HttpExchange x) throws IOException {
    headers(x, "application/json; charset=utf-8");
    try (Connection c = new ConnectionFactory().getConnection()) {
      if ("GET".equals(x.getRequestMethod())) {
        try (PreparedStatement p = c.prepareStatement("SELECT payload FROM rodada_estado WHERE id=1"); ResultSet r = p.executeQuery()) {
          send(x, 200, r.next() ? r.getString(1) : "{}");
        }
      } else if ("PUT".equals(x.getRequestMethod())) {
        String payload = new String(x.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (payload.length() > 1_000_000) { send(x, 413, "{\"error\":\"Estado muito grande\"}"); return; }
        try (PreparedStatement p = c.prepareStatement("INSERT INTO rodada_estado(id,payload) VALUES(1,?) ON DUPLICATE KEY UPDATE payload=VALUES(payload)")) { p.setString(1, payload); p.executeUpdate(); }
        send(x, 204, "");
      } else send(x, 405, "{\"error\":\"Método não permitido\"}");
    } catch (Exception e) { send(x, 500, "{\"error\":\"Erro ao acessar a rodada\"}"); }
  }
  private static void staticFile(HttpExchange x) throws IOException {
    String path = x.getRequestURI().getPath(); if (path.equals("/")) path = "/index.html";
    if (path.contains("..")) { send(x, 403, "Acesso negado"); return; }
    try (InputStream in = WebServer.class.getResourceAsStream("/static" + path)) {
      if (in == null) { send(x, 404, "Não encontrado"); return; }
      headers(x, mime(path));
      send(x, 200, in.readAllBytes());
    }
  }
  private static String mime(String p) { return p.endsWith(".css") ? "text/css; charset=utf-8" : p.endsWith(".js") ? "application/javascript; charset=utf-8" : "text/html; charset=utf-8"; }
  private static void headers(HttpExchange x, String type) { x.getResponseHeaders().set("Content-Type", type); x.getResponseHeaders().set("Cache-Control", "no-store"); }
  private static void send(HttpExchange x, int code, String value) throws IOException { send(x, code, value.getBytes(StandardCharsets.UTF_8)); }
  private static void send(HttpExchange x, int code, byte[] value) throws IOException { x.sendResponseHeaders(code, code == 204 ? -1 : value.length); if (code != 204) try (OutputStream o=x.getResponseBody()) { o.write(value); } }
}
