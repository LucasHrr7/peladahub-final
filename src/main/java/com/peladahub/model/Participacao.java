package com.peladahub.model;

public class Participacao {
    private Integer id;
    private Integer jogadorId;
    private Integer peladaId;


    public Participacao() {
    }

    public Participacao(Integer jogadorId, Integer peladaId) {
        this.jogadorId = jogadorId;
        this.peladaId = peladaId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJogadorId() {
        return jogadorId;
    }

    public void setJogadorId(Integer jogadorId) {
        this.jogadorId = jogadorId;
    }

    public Integer getPeladaId() {
        return peladaId;
    }

    public void setPeladaId(Integer peladaId) {
        this.peladaId = peladaId;
    }
}
