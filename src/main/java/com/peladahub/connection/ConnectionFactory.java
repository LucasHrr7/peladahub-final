package com.peladahub.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public Connection getConnection() {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/pelada_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");
        
        try { 
            // Força o carregamento do Driver do MySQL para o Java encontrá-lo no Railway
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            
            return DriverManager.getConnection(url, user, password); 
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do MySQL não encontrado no classpath!", e);
        }
        catch (SQLException e) { 
            throw new RuntimeException("Não foi possível conectar ao banco. Configure DB_URL, DB_USER e DB_PASSWORD.", e); 
        }
    }
}