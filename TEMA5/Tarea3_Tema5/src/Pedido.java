import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    private String codigo;      // Código del pedido

    private Long idCliente;     // ID del cliente

    @OneToMany(cascade = CascadeType.ALL)
    private List<Producto> productos = new ArrayList<>();

    public Pedido() {}

    public Pedido(String codigo, Long idCliente, List<Producto> productos) {
        this.codigo = codigo;
        this.idCliente = idCliente;
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "codigo='" + codigo + '\'' +
                ", idCliente=" + idCliente +
                ", productos=" + productos +
                '}';
    }

    public  String getCodigo() {return codigo;}
    public void setCodigo(String codigo) { this.codigo = codigo;}

    public Long getIdCliente() {return idCliente;}
    public void setIdCliente(Long idCliente) {this.idCliente = idCliente;}

    public List<Producto> getProductos() {return productos;}
    public void setProductos(List<Producto> productos) {this.productos = productos;}
}
