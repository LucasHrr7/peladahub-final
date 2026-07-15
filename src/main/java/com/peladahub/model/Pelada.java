package com.peladahub.model;

import java.sql.Date;
import java.sql.Time;



public class Pelada {
    private Integer id;
    private String nome;
    private String local;
    private Date dataJogo;
    private Time horarioJogo;
    private Integer maxJogadores;

 
 
     public Pelada() {
    
    }

    public Pelada(String nome, String local, Date dataJogo, Time horarioJogo, Integer maxJogadores) {
        this.nome = nome;
        this.local = local;
        this.dataJogo = dataJogo;
        this.horarioJogo = horarioJogo;
        this.maxJogadores = maxJogadores;
    }

        public Integer getId() {
        return id;
        }     

        public void setId(Integer id) {
        this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getLocal() {
                return local;
        }   

        public void setLocal(String local) {
                this.local = local;
        }   

        public Date getDataJogo() {
                return dataJogo;
        }                           

        public void setDataJogo(Date dataJogo) {
                this.dataJogo = dataJogo;
        }

        public Time getHorarioJogo() {
                return horarioJogo;
        }

        public void setHorarioJogo(Time horarioJogo) {
                this.horarioJogo = horarioJogo;
        }

        public Integer getMaxJogadores() {
                return maxJogadores;
        }

        public void setMaxJogadores(Integer maxJogadores) {
                this.maxJogadores = maxJogadores;
        }
}
