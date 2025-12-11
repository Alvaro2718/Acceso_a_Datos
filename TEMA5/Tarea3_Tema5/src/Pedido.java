import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity  //Declaramos la clase como entidad
public class Pedido {

    /// ATRIBUTOS
    @Id
    @GeneratedValue //El codigo del pedido es generado automáticamente
    private Long codigo;      // Código del pedido el enunciado pide que sea un String pero no lo veo lógico

    private Long idCliente;     // ID del cliente

    //Relación de uno a muchos, un pedido puede tener muchos productos
    @OneToMany(cascade = CascadeType.ALL)
    private List<Producto> productos = new ArrayList<>();


    /// //CONSTRUCTOR VACÍO OBLIGATORIO
    public Pedido() {}

    /// CONSTRUCTOR PRINCIPAL CON PARÁMETROS
    public Pedido( Long idCliente, List<Producto> productos) {

        this.idCliente = idCliente;
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "PEDIDO" +
                "\nCodigo=" + codigo +
                "\nIdCliente=" + idCliente +
                "\nProductos=" + productos +
                "\n------------------------";
    }

    public  long getCodigo() {return codigo;}
    public void setCodigo(long codigo) { this.codigo = codigo;}

    public Long getIdCliente() {return idCliente;}
    public void setIdCliente(Long idCliente) {this.idCliente = idCliente;}

    public List<Producto> getProductos() {return productos;}
    public void setProductos(List<Producto> productos) {this.productos = productos;}
}
