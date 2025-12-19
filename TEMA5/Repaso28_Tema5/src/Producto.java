import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Producto implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private String nombre;
    private double precio;
    private int stock;

    public Producto() {}

    public Producto(String nombre, double precio, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return id + " - " + nombre + " | " + precio + "€ | Stock: " + stock;
    }
}
