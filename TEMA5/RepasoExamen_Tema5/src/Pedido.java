import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue
    private Long id;

    private String codigo;
    private Long idCliente;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Producto> productos = new ArrayList<>();

    // Constructor vacío obligatorio
    public Pedido() {}

    public Pedido(String codigo, Long idCliente, List<Producto> productos) {
        this.codigo = codigo;
        this.idCliente = idCliente;
        this.productos = productos;
    }

    // Getters y setters
    public Long getId() { return id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}

