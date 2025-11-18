package org.example;

import java.io.Serializable;

public class LlamadasEmitida implements Serializable {
    private int codigoLlamada;
    private int numeroLlamado;
    private int duracionLlamada;
    private float importeLlamada;
    private int simLlamante; // clave foránea a TarjetaTelefonica

    // Constructor vacío (necesario para Hibernate)
    public LlamadasEmitida() {
    }

    // Constructor con parámetros
    public LlamadasEmitida(int codigoLlamada, int numeroLlamado, int duracionLlamada, float importeLlamada, int simLlamante) {
        this.codigoLlamada = codigoLlamada;
        this.numeroLlamado = numeroLlamado;
        this.duracionLlamada = duracionLlamada;
        this.importeLlamada = importeLlamada;
        this.simLlamante = simLlamante;
    }

    // Getters y Setters
    public int getCodigoLlamada() {
        return codigoLlamada;
    }

    public void setCodigoLlamada(int codigoLlamada) {
        this.codigoLlamada = codigoLlamada;
    }

    public int getNumeroLlamado() {
        return numeroLlamado;
    }

    public void setNumeroLlamado(int numeroLlamado) {
        this.numeroLlamado = numeroLlamado;
    }

    public int getDuracionLlamada() {
        return duracionLlamada;
    }

    public void setDuracionLlamada(int duracionLlamada) {
        this.duracionLlamada = duracionLlamada;
    }

    public float getImporteLlamada() {
        return importeLlamada;
    }

    public void setImporteLlamada(float importeLlamada) {
        this.importeLlamada = importeLlamada;
    }

    public int getSimLlamante() {
        return simLlamante;
    }

    public void setSimLlamante(int simLlamante) {
        this.simLlamante = simLlamante;
    }
}
