package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "TARJETAS_TELEFONICAS")
public class TarjetasTelefonica {
    @Id
    @Column(name = "NUMERO_SIM", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CODIGO_AGENTE_ASOCIADO", nullable = false)
    private Agente codigoAgenteAsociado;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Agente getCodigoAgenteAsociado() {
        return codigoAgenteAsociado;
    }

    public void setCodigoAgenteAsociado(Agente codigoAgenteAsociado) {
        this.codigoAgenteAsociado = codigoAgenteAsociado;
    }

}