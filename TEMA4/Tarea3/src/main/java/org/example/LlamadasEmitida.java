package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "LLAMADAS_EMITIDAS")
public class LlamadasEmitida {
    @Id
    @Column(name = "CODIGO_LLAMADA", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SIM_LLAMANTE", nullable = false)
    private TarjetasTelefonica simLlamante;

    @Column(name = "NUMERO_LLAMADO", nullable = false)
    private Integer numeroLlamado;

    @Column(name = "DURACION_LLAMADA", nullable = false)
    private Integer duracionLlamada;

    @Column(name = "IMPORTE_LLAMADA", nullable = false)
    private Float importeLlamada;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TarjetasTelefonica getSimLlamante() {
        return simLlamante;
    }

    public void setSimLlamante(TarjetasTelefonica simLlamante) {
        this.simLlamante = simLlamante;
    }

    public Integer getNumeroLlamado() {
        return numeroLlamado;
    }

    public void setNumeroLlamado(Integer numeroLlamado) {
        this.numeroLlamado = numeroLlamado;
    }

    public Integer getDuracionLlamada() {
        return duracionLlamada;
    }

    public void setDuracionLlamada(Integer duracionLlamada) {
        this.duracionLlamada = duracionLlamada;
    }

    public Float getImporteLlamada() {
        return importeLlamada;
    }

    public void setImporteLlamada(Float importeLlamada) {
        this.importeLlamada = importeLlamada;
    }

}