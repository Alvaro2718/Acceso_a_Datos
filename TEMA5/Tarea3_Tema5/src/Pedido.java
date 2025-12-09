import jakarta.persistence.*;

import java.util.List;

@Entity
public class Pedido {


    @Id
    @GeneratedValue
    private String codigo;
    private long idCliente;
    private List<Producto> productos;

    public Pedido() {}

    public Pedido( long idCliente, List<Producto> productos) {
        this.idCliente = idCliente;
        this.productos = productos;
    }

    public String getCodigo() {return this.codigo;}
    public void setCodigo(String codigo) {this.codigo = codigo;}

    public long getIdCliente() {return this.idCliente;}
    public void setIdCliente(long idCliente) {this.idCliente = idCliente;}

    public List<Producto> getProductos() {return this.productos;}
    public void setProductos(List<Producto> productos) {this.productos = productos;}

    @Override
    public String toString() {return (" \n" + codigo +"\n"+ idCliente +"\n"+ productos);}





}
