package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AGENTES")
public class Agente {
    @Id
    @Column(name = "CODIGO_AGENTE", nullable = false)
    private Integer id;

    @Column(name = "NOMBRE_AGENTE", nullable = false, length = 40)
    private String nombreAgente;

    @Column(name = "FRASE_CLAVE", nullable = false, length = 60)
    private String fraseClave;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreAgente() {
        return nombreAgente;
    }

    public void setNombreAgente(String nombreAgente) {
        this.nombreAgente = nombreAgente;
    }

    public String getFraseClave() {
        return fraseClave;
    }

    public void setFraseClave(String fraseClave) {
        this.fraseClave = fraseClave;
    }

}