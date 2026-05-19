package com.example.wattwiseapp;

public class Room {

    private int idComodo;
    private String nomeComodo;
    private String tipoComodo;
    private String qtdTomadas;
    private String descricao;


    public Room() {

    }

    //constructor
    public Room(String idComodo, String nomeComodo, String tipoComodo, String qtdTomadas, String descricao) {
        this.idComodo = idComodo;
        this.nomeComodo = nomeComodo;
        this.tipoComodo = tipoComodo;
        this.qtdTomadas = qtdTomadas;
        this.descricao = descricao;
    }

    //get e set
    public String getIdComodo() {
        return idComodo;
    }

    public void setIdComodo(String idComodo) {
        this.idComodo = idComodo;
    }

    public String getNomeComodo() {
        return nomeComodo;
    }

    public void setNomeComodo(String nomeComodo) {
        this.nomeComodo = nomeComodo;
    }

    public String getTipoComodo() {
        return tipoComodo;
    }

    public void setTipoComodo(String tipoComodo) {
        this.tipoComodo = tipoComodo;
    }

    public String getQtdTomadas() {
        return qtdTomadas;
    }

    public void setQtdTomadas(String qtdTomadas) {
        this.qtdTomadas = qtdTomadas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
