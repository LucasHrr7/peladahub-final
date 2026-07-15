package com.peladahub.dao;

import java.sql.Connection;
import com.peladahub.connection.ConnectionFactory;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.peladahub.model.Participacao;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;




public class ParticipacaoDAO {

  private Connection connection;

    public ParticipacaoDAO() {
        this.connection = new ConnectionFactory().getConnection();
    
    }


    public void cadastrarParticipacao(Integer jogadorId, Integer peladaId) {
        String sqlVerificar = " SELECT COUNT(*) AS TOTAL, p.max_jogadores FROM participacao pa JOIN pelada p ON pa.pelada_id = p.id WHERE pa.pelada_id = ? ";
        String sqlDuplicado = "SELECT COUNT(*) AS TOTAL FROM participacao WHERE jogador_id = ? AND pelada_id = ?";       
        String sqlInserir = "INSERT INTO participacao (jogador_id, pelada_id) VALUES (?, ?)";

       
       
       try {
           
        PreparedStatement psDuplicado = connection.prepareStatement(sqlDuplicado);  
        psDuplicado.setInt(1, jogadorId);
        psDuplicado.setInt(2, peladaId);

        ResultSet rsDuplicado = psDuplicado.executeQuery();
          
        if (rsDuplicado.next() && rsDuplicado.getInt("TOTAL") > 0) {
            System.out.println("O jogador já está cadastrado para esta pelada.");
            return;
        }
        
          
        
        PreparedStatement psVerificar = connection.prepareStatement(sqlVerificar);
            psVerificar.setInt(1, peladaId);
            ResultSet rs = psVerificar.executeQuery();


         int total = 0;
         int maxJogadores = 0;


            if(rs.next()) {
                 total = rs.getInt("TOTAL");
                 maxJogadores =  rs.getInt("max_jogadores");
            }

            if (total >= maxJogadores) {
                System.out.println("Não é possível cadastrar participação. A pelada já atingiu o número máximo de jogadores.");
                return;
            } 
            
            PreparedStatement psInserir = connection.prepareStatement(sqlInserir);  
            psInserir.setInt(1, jogadorId);
            psInserir.setInt(2, peladaId);
            psInserir.executeUpdate();

            System.out.println("Participação cadastrada com sucesso!");
        
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar participação: " + e.getMessage());
        }
    }



    public void listarParticipantes(int peladaId) {
        String sql  = """
                        SELECT j.nome, j.posicao
                        FROM participacao p
                        JOIN jogador j ON p.jogador_id = j.id
                        WHERE p.pelada_id = ?
                     """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, peladaId);

            ResultSet rs = ps.executeQuery();

            System.out.println("Participantes da Pelada ID " + peladaId + ":");
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String posicao = rs.getString("posicao");
                System.out.println("Nome: " + nome + ", Posição: " + posicao);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar participantes: " + e.getMessage());
        }

  }




    public void confirmarPresenca(int jogadorId, int peladaId) {
        String sql = "UPDATE participacao SET confirmado = TRUE WHERE jogador_id = ? AND pelada_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, jogadorId);
            ps.setInt(2, peladaId);
            
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Presença confirmada com sucesso!");
            } else {
                System.out.println("Participação não encontrada para confirmar presença.");
            }
        
        } catch (SQLException e) {
            System.out.println("Erro ao confirmar presença: " + e.getMessage());
        }
    }  
    
    
    
    public void cancelarPresenca(int jogadorId, int peladaId) {
        String sql = "UPDATE participacao SET confirmado = FALSE WHERE jogador_id = ? AND pelada_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, jogadorId);
            ps.setInt(2, peladaId);
            
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Presença cancelada com sucesso!");
            } else {
                System.out.println("Participação não encontrada para cancelar presença.");
            }
        
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar presença: " + e.getMessage());
        }
    }

    public void removerParticipacao(int jogadorId, int peladaId) {
        String sql = "DELETE FROM participacao WHERE jogador_id = ? AND pelada_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, jogadorId);
            ps.setInt(2, peladaId);
            
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Participação removida com sucesso!");
            } else {
                System.out.println("Participação não encontrada para remoção.");
            }
        
        } catch (SQLException e) {
            System.out.println("Erro ao remover participação: " + e.getMessage());
        }
    }
   
   
    public void listarNaoConfirmados(int peladaId) {
        String sql = """
                        SELECT j.nome, j.posicao
                        FROM participacao p
                        JOIN jogador j ON p.jogador_id = j.id
                        WHERE p.pelada_id = ? AND p.confirmado = FALSE
                     """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, peladaId);
            ResultSet rs = ps.executeQuery();

            System.out.println("Jogadores não confirmados para Pelada ID " + peladaId + ":");
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String posicao = rs.getString("posicao");
                System.out.println("Nome: " + nome + ", Posição: " + posicao);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar não confirmados: " + e.getMessage());
        }
    }               


    public void listarConfirmados(int peladaId) {
        String sql = """
                        SELECT j.nome, j.posicao
                        FROM participacao p
                        JOIN jogador j ON p.jogador_id = j.id
                        WHERE p.pelada_id = ? AND p.confirmado = TRUE
                     """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, peladaId);
            ResultSet rs = ps.executeQuery();

            System.out.println("Jogadores confirmados para Pelada ID " + peladaId + ":");
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String posicao = rs.getString("posicao");
                System.out.println("Nome: " + nome + ", Posição: " + posicao);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar confirmados: " + e.getMessage());
        }
    }


    public void sortearTimes(int peladaId) {
        String sql = """
                        SELECT j.nome, j.posicao
                        FROM participacao p
                        JOIN jogador j ON p.jogador_id = j.id
                        WHERE p.pelada_id = ? AND p.confirmado = TRUE
                     """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, peladaId);
            ResultSet rs = ps.executeQuery();

            ArrayList<String> jogadores = new ArrayList<>();
            while (rs.next()) {
                String nome = rs.getString("nome");
                jogadores.add(nome);
            }
            
            Collections.shuffle(jogadores);
            System.out.println("-----TIME A-----");
            
            for (int i = 0; i < jogadores.size(); i+=2) {
                System.out.println(jogadores.get(i));
            }
           
            System.out.println();

            System.out.println("-----TIME B-----");
            
            for (int i = 1; i < jogadores.size(); i+=2) {
                System.out.println(jogadores.get(i));
           
            }
        } catch (SQLException e) {
            System.out.println("Erro ao sortear times: " + e.getMessage());
        }
    
    }

    public void mostrarVagasRestantes(int peladaId) {
        String sql = "SELECT COUNT(*) AS total , p.max_jogadores FROM participacao pa JOIN pelada p ON pa.pelada_id = p.id WHERE pa.pelada_id = ?"; 
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, peladaId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                int maxJogadores = rs.getInt("max_jogadores");
                int vagasRestantes = maxJogadores - total;
                System.out.println("Vagas restantes para Pelada ID " + peladaId + ": " + vagasRestantes);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao mostrar vagas restantes: " + e.getMessage());
        }   
     }
  }