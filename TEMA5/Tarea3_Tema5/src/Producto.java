import java.io.Serializable;
import jakarta.persistence.*;

@Entity
public class Producto implements Serializable {

    @Id @GeneratedValue
    private Integer ID;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(){

    }
    public Producto( String nombre, double precio, int stock) {

        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return String.format("%d, %s, %.2f, %d", ID, nombre, precio, stock);
    }
}

