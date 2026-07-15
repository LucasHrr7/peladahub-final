package com.peladahub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.peladahub.connection.ConnectionFactory;
import com.peladahub.model.Pelada;



public class PeladaDAO {

  private Connection connection;

    public PeladaDAO() {
        this.connection = new ConnectionFactory().getConnection();
    
    }
    
    public void cadastrarPelada(Pelada pelada) {
       
        String sql = "INSERT INTO pelada (nome, local, data_jogo, horario_jogo, max_jogadores) VALUES (?, ?, ?, ?, ?)";


        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, pelada.getNome());
            ps.setString(2, pelada.getLocal());
            ps.setDate(3, pelada.getDataJogo());
            ps.setTime(4, pelada.getHorarioJogo());
            ps.setInt(5, pelada.getMaxJogadores());
           
            ps.executeUpdate();

            System.out.println("Pelada cadastrada com sucesso!");
        
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar pelada: " + e.getMessage());

        }

    }


    public void listarPeladas() {
       
        String sql = "SELECT * FROM pelada";
       
        try {
            
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String local = rs.getString("local");
                java.sql.Date dataJogo = rs.getDate("data_jogo");
                java.sql.Time horarioJogo = rs.getTime("horario_jogo");
                int maxJogadores = rs.getInt("max_jogadores");

                System.out.println("ID: " + id + ", Nome: " + nome + ", Local: " + local + ", Data: " + dataJogo + ", Horário: " + horarioJogo + ", Max Jogadores: " + maxJogadores);       
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar peladas: " + e.getMessage());
        }
    }


    public void editarPelada(Pelada pelada){
        String sql = "UPDATE pelada SET nome = ?, local = ?, data_jogo = ?, horario_jogo = ?, max_jogadores = ? WHERE id = ?"; 
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, pelada.getNome());
            ps.setString(2, pelada.getLocal());
            ps.setDate(3, pelada.getDataJogo());
            ps.setTime(4, pelada.getHorarioJogo());
            ps.setInt(5, pelada.getMaxJogadores());
            ps.setInt(6, pelada.getId());

            ps.executeUpdate();

            System.out.println("Pelada editada com sucesso!");
        
        } catch (SQLException e) {
            System.out.println("Erro ao editar pelada: " + e.getMessage());
        }        
    }

     public void excluirPelada(int id) {
        String sqlParticipacao = "DELETE FROM participacao WHERE pelada_id = ?";
        String sqlPelada = "DELETE FROM pelada WHERE id = ?";

        try {
            PreparedStatement psParticipacao = connection.prepareStatement(sqlParticipacao);
            psParticipacao.setInt(1, id);
            psParticipacao.executeUpdate();

            PreparedStatement psPelada = connection.prepareStatement(sqlPelada);
            psPelada.setInt(1, id);
            int rowsDeleted = psPelada.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Pelada excluída com sucesso!");
            } else {
                System.out.println("Nenhuma pelada encontrada com o ID fornecido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir pelada: " + e.getMessage());
        }       
   } 
}