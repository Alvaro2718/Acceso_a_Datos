import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Scanner;

public class InventarioApp {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        emf = Persistence.createEntityManagerFactory("C:\\objectdb-2.9.4︃\\db︃\\Tarea2_Tema5.odb");
        em = emf.createEntityManager();

        guardarProductosIniciales();  // Productos de ejemplo

        int opcion;

        do {
            mostrarMenu();
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> añadirProducto();
                case 2 -> listarProductos();
                case 3 -> buscarProductoPorNombre();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        em.close();
        emf.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n---- SISTEMA DE INVENTARIO ODB ----");
        System.out.println("1. Añadir producto");
        System.out.println("2. Listar productos");
        System.out.println("3. Buscar producto por nombre");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void guardarProductosIniciales() {
        em.getTransaction().begin();

        em.persist(new Producto("Oro", 3575.99, 10));
        em.persist(new Producto("Plata", 57.99, 88));
        em.persist(new Producto("Platino", 999.99, 99));
        em.persist(new Producto("Consola X", 499.99, 20));
        em.persist(new Producto("Juego Y", 59.99, 50));

        em.getTransaction().commit();
    }

    private static void añadirProducto() {
        System.out.println("\n--- Añadir Producto ---");

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Precio: ");
        double precio = sc.nextDouble();

        System.out.print("Stock: ");
        int stock = sc.nextInt();
        sc.nextLine();

        Producto p = new Producto(nombre, precio, stock);

        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();

        System.out.println("Producto añadido correctamente.");
    }

    private static void listarProductos() {
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

    private static void buscarProductoPorNombre() {
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
