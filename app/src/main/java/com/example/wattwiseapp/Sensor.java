package com.example.wattwiseapp;

public class Sensor {

    private String idSensor;
    private String idEletroAtivo;

    public Sensor() {

    }

    public Sensor(String idEletroAtivo, String idSensor) {
        this.idEletroAtivo = idEletroAtivo;
        this.idSensor = idSensor;
    }

    public String getIdEletroAtivo() {
        return idEletroAtivo;
    }

    public void setIdEletroAtivo(String idEletroAtivo) {
        this.idEletroAtivo = idEletroAtivo;
    }

    public String getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(String idSensor) {
        this.idSensor = idSensor;
    }
}
