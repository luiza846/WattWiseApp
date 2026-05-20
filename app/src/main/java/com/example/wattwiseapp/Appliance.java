package com.example.wattwiseapp;

public class Appliance {

    private String idEletro;
    private String nomeEletro;
    private String TipoEletro;
    private String comodoEletro;
    private String potenciaEletro;
    private String descricaoEletro;

    //construtor
    public Appliance(String idEletro, String nomeEletro, String tipoEletro, String comodoEletro, String potenciaEletro, String descricaoEletro) {
        this.idEletro = idEletro;
        this.nomeEletro = nomeEletro;
        TipoEletro = tipoEletro;
        this.comodoEletro = comodoEletro;
        this.potenciaEletro = potenciaEletro;
        this.descricaoEletro = descricaoEletro;
    }

    //get e set
    public String getIdEletro() {
        return idEletro;
    }

    public void setIdEletro(String idEletro) {
        this.idEletro = idEletro;
    }

    public String getNomeEletro() {
        return nomeEletro;
    }

    public void setNomeEletro(String nomeEletro) {
        this.nomeEletro = nomeEletro;
    }

    public String getTipoEletro() {
        return TipoEletro;
    }

    public void setTipoEletro(String tipoEletro) {
        TipoEletro = tipoEletro;
    }

    public String getComodoEletro() {
        return comodoEletro;
    }

    public void setComodoEletro(String comodoEletro) {
        this.comodoEletro = comodoEletro;
    }

    public String getPotenciaEletro() {
        return potenciaEletro;
    }

    public void setPotenciaEletro(String potenciaEletro) {
        this.potenciaEletro = potenciaEletro;
    }

    public String getDescricaoEletro() {
        return descricaoEletro;
    }

    public void setDescricaoEletro(String descricaoEletro) {
        this.descricaoEletro = descricaoEletro;
    }
}
