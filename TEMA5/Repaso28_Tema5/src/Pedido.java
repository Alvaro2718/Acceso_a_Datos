import jakarta.persistence.*;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue
    private Long codigo;

    private Long idCliente;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Producto> productos;

    public Pedido() {}

    public Pedido(Long idCliente, List<Producto> productos) {
        this.idCliente = idCliente;
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "\nPEDIDO " + codigo +
                "\nCliente: " + idCliente +
                "\nProductos: " + productos +
                "\n-----------------------";
    }
}

