import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PedidosApp {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        emf = Persistence.createEntityManagerFactory("C:\\objectdb-2.9.4\\db\\InventarioObjetosCompuestos.odb");
        em = emf.createEntityManager();



        em.close();
        emf.close();
    }


    private static void añadirPedido() {
        System.out.println("\n--- Añadir Producto ---");

        List<Producto> productos = new ArrayList<>();
        Producto producto = new Producto( "Oro", 32, 32);

        Pedido pedido = new Pedido(4, productos);

        em.getTransaction().begin();
        em.persist(producto);
        em.getTransaction().commit();

        System.out.println("Producto añadido correctamente.");
    }

    private static void listarPedidos() {
        System.out.println("\n--- Lista de Productos ---");

        List<Producto> productos = em
                .createQuery("SELECT p FROM Producto p", Producto.class)
                .getResultList();

        if (productos.isEmpty()) {
            System.out.println("No hay productos en el inventario.");
            return;
        }

        productos.forEach(System.out::println);
    }

    private static void buscarPedidoPorNombre() {
        System.out.print("\nIngrese el nombre del producto a buscar: ");
        String nombre = sc.nextLine();

        List<Producto> productos = em.createQuery(
                        "SELECT p FROM Producto p WHERE p.nombre LIKE :n", Producto.class)
                .setParameter("n", "%" + nombre + "%")
                .getResultList();

        if (productos.isEmpty()) {
            System.out.println("No se encontró ningún producto con ese nombre.");
        } else {
            productos.forEach(System.out::println);
        }
    }
}