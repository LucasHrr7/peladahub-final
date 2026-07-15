package com.peladahub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.peladahub.connection.ConnectionFactory;
import com.peladahub.model.Jogador;




public class JogadorDAO {

  private Connection connection;

    public JogadorDAO() {
        this.connection = new ConnectionFactory().getConnection();
    
    }
 
    public void cadastrarJogador(Jogador jogador) {
        String sql = "INSERT INTO jogador  (nome, posicao)  VALUES (?, ?)";
        

        try {
            PreparedStatement ps = connection.prepareStatement(sql); 
            ps.setString(1, jogador.getNome()); 
            ps.setString(2, jogador.getPosicao());
            
            ps.executeUpdate();

            System.out.println("Jogador cadastrado com sucesso!");

            
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar jogador: " + e.getMessage());
        }
    }


    public void listarJogadores() {
        String sql = "SELECT * FROM jogador";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String posicao = rs.getString("posicao");

                System.out.println("ID: " + id + ", Nome: " + nome + ", Posição: " + posicao);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar jogadores: " + e.getMessage());  
  }
 }
    
    
    public void editarJogador(int id, String nome, String posicao) {
        String sql = "UPDATE jogador SET nome = ?, posicao = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, posicao);
            ps.setInt(3, id);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Jogador atualizado com sucesso!");
            } else {
                System.out.println("Nenhum jogador encontrado com o ID fornecido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao editar jogador: " + e.getMessage());
        }

    }

    public void excluirJogador(int id) {
        String sqlParticipacao = "DELETE FROM participacao WHERE jogador_id = ?";
        String sqlJogador = "DELETE FROM jogador WHERE id = ?";

        try {
            // Excluir participações relacionadas
            PreparedStatement psParticipacao = connection.prepareStatement(sqlParticipacao);
            psParticipacao.setInt(1, id);
            psParticipacao.executeUpdate();

            // Excluir o jogador
            PreparedStatement psJogador = connection.prepareStatement(sqlJogador);
            psJogador.setInt(1, id);
            int rowsDeleted = psJogador.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Jogador excluído com sucesso!");
            } else {
                System.out.println("Nenhum jogador encontrado com o ID fornecido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir jogador: " + e.getMessage());
        }                       
     }       
}