import jakarta.persistence.*;
import java.util.*;

public class PedidoApp {

    // Scanner para leer datos por teclado
    private static final Scanner sc = new Scanner(System.in);

    // Objetos para la conexión con la base de datos ObjectDB
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {

        // Creamos la conexión con la base de datos .odb
        emf = Persistence.createEntityManagerFactory("db/inventarioPedidos.odb");
        em = emf.createEntityManager();

        // Insertamos algunos productos de ejemplo al iniciar el programa
        insertarProductosIniciales();

        int opcion;
        do {
            // Mostramos el menú
            mostrarMenu();
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar el buffer del Scanner

            // Ejecutamos la opción elegida
            switch (opcion) {
                case 1 -> añadirProducto();
                case 2 -> listarProductos();
                case 3 -> buscarProductoPorNombre();
                case 4 -> productosConStockBajo();
                case 5 -> actualizarPrecioProducto();
                case 6 -> crearPedido();
                case 7 -> listarPedidos();
                case 8 -> eliminarProducto();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);

        // Cerramos la conexión con la base de datos
        em.close();
        emf.close();
    }

    // ---------------- MÉTODOS ----------------

    // Muestra el menú principal por pantalla
    private static void mostrarMenu() {
        System.out.println("""
        \n--- MENÚ PRINCIPAL ---
        1. Añadir producto
        2. Listar productos
        3. Buscar producto por nombre
        4. Productos con stock bajo
        5. Actualizar precio producto
        6. Crear pedido
        7. Listar pedidos
        0. Salir
        """);
    }

    // Inserta algunos productos iniciales en la base de datos
    private static void insertarProductosIniciales() {
        em.getTransaction().begin();
        em.persist(new Producto("Teclado", 40, 10));
        em.persist(new Producto("Ratón", 20, 15));
        em.persist(new Producto("Monitor", 180, 5));
        em.getTransaction().commit();
    }

    // Pide los datos por teclado y guarda un nuevo producto
    private static void añadirProducto() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Precio: ");
        double precio = sc.nextDouble();
        System.out.print("Stock: ");
        int stock = sc.nextInt();
        sc.nextLine();

        em.getTransaction().begin();
        em.persist(new Producto(nombre, precio, stock));
        em.getTransaction().commit();
    }

    // Muestra todos los productos guardados en la base de datos
    private static void listarProductos() {
        em.createQuery("SELECT p FROM Producto p", Producto.class)
                .getResultList()
                .forEach(System.out::println);
    }

    // Busca productos por nombre usando LIKE
    private static void buscarProductoPorNombre() {
        System.out.print("Nombre a buscar: ");
        String nombre = sc.nextLine();

        em.createQuery(
                        "SELECT p FROM Producto p WHERE p.nombre LIKE :n", Producto.class)
                .setParameter("n", "%" + nombre + "%")
                .getResultList()
                .forEach(System.out::println);
    }

    // Muestra los productos que tienen menos de 20 unidades en stock
    private static void productosConStockBajo() {
        em.createQuery(
                        "SELECT p FROM Producto p WHERE p.stock < 20", Producto.class)
                .getResultList()
                .forEach(System.out::println);
    }

    // Actualiza el precio de un producto a partir de su ID
    private static void actualizarPrecioProducto() {
        System.out.print("ID producto: ");
        int id = sc.nextInt();
        System.out.print("Nuevo precio: ");
        double precio = sc.nextDouble();
        sc.nextLine();

        em.getTransaction().begin();
        Producto p = em.find(Producto.class, id);
        if (p != null) {
            p.setPrecio(precio);
            System.out.println("Precio actualizado correctamente.");
        } else {
            System.out.println("Producto no encontrado.");
        }
        em.getTransaction().commit();
    }

    // Crea un pedido seleccionando productos existentes
    private static void crearPedido() {
        // Obtenemos todos los productos de la base de datos
        List<Producto> productos = em.createQuery(
                        "SELECT p FROM Producto p", Producto.class)
                .getResultList();

        if (productos.isEmpty()) {
            System.out.println("No hay productos disponibles.");
            return;
        }

        // Mostramos los productos para poder seleccionarlos
        productos.forEach(p ->
                System.out.println(p.getId() + ". " + p));

        System.out.print("ID cliente: ");
        Long idCliente = sc.nextLong();

        List<Producto> seleccionados = new ArrayList<>();
        int op;
        do {
            System.out.print("ID producto (0 terminar): ");
            op = sc.nextInt();
            if (op != 0) {
                Producto prod = em.find(Producto.class, op);
                if (prod != null) {
                    seleccionados.add(prod);
                    System.out.println("Producto añadido.");
                }
            }
        } while (op != 0);

        // Guardamos el pedido en la base de datos
        em.getTransaction().begin();
        em.persist(new Pedido(idCliente, seleccionados));
        em.getTransaction().commit();

        System.out.println("Pedido guardado correctamente.");
    }

    // Muestra todos los pedidos guardados
    private static void listarPedidos() {
        em.createQuery("SELECT p FROM Pedido p", Pedido.class)
                .getResultList()
                .forEach(System.out::println);
    }
    // Elimina un producto de la base de datos a partir de su ID
    private static void eliminarProducto() {
        System.out.print("ID del producto a eliminar: ");
        int id = sc.nextInt();
        sc.nextLine();

        em.getTransaction().begin();
        Producto p = em.find(Producto.class, id);

        if (p != null) {
            em.remove(p);
            System.out.println("Producto eliminado correctamente.");
        } else {
            System.out.println("Producto no encontrado.");
        }

        em.getTransaction().commit();
    }

}
