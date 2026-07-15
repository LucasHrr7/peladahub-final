
package com.peladahub;

import java.sql.Connection;
import java.sql.SQLException;
import com.peladahub.connection.ConnectionFactory;


public class TesteConexao {
   
    public static void main(String... x) {
        try (Connection connection = new ConnectionFactory().getConnection()) {
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao estabelecer conexão: " + e.getMessage());
        }
    }
}
