import jakarta.persistence.*;
import java.util.*;

public class PedidosApp {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("C:/objectdb-2.9.4/db/pedidos.odb");
        EntityManager em = emf.createEntityManager();
        Scanner sc = new Scanner(System.in);

        int opcion;

        // Menú del programa
        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Crear un nuevo pedido");
            System.out.println("2. Mostrar todos los pedidos");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            opcion = sc.nextInt();

            switch (opcion) {

                case 1:
                    crearPedido(em, sc);
                    break;

                case 2:
                    mostrarTodosLosPedidos(em);
                    break;

                case 3:
                    System.out.println("Saliendo del programa...");
                    // Cerrar conexiones
                    em.close();
                    emf.close();
                    System.out.println("\nConexión cerrada. Fin del programa.");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 3);

    }

    // Método para crear un pedido
    public static void crearPedido(EntityManager em, Scanner sc) {

        // Lista de productos predefinidos
        List<Producto> listaProductos = new ArrayList<>();
        listaProductos.add(new Producto("Teclado ordenador", 39.99, 10));
        listaProductos.add(new Producto("Ratón", 19.99, 15));
        listaProductos.add(new Producto("Auriculares", 29.99, 8));

        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");
        for (int i = 0; i < listaProductos.size(); i++) {
            System.out.println((i + 1) + ". " + listaProductos.get(i));
        }

        // Pedir ID de cliente
        System.out.print("\nIntroduce el ID del cliente: ");
        Long idCliente = sc.nextLong();

        // Selección de productos
        List<Producto> productosSeleccionados = new ArrayList<>();
        int opcion;

        do {
            System.out.print("\nSelecciona un producto por número (0 para terminar): ");
            opcion = sc.nextInt();

            if (opcion > 0 && opcion <= listaProductos.size()) {
                productosSeleccionados.add(listaProductos.get(opcion - 1));
                System.out.println("Producto añadido.");
            }

        } while (opcion != 0);

        // Crear pedido
        Pedido pedido = new Pedido(idCliente, productosSeleccionados);

        // Guardar en la base de datos
        em.getTransaction().begin();
        em.persist(pedido);
        em.getTransaction().commit();

        System.out.println("\nPedido guardado correctamente:");
        System.out.println(pedido);
    }

    // Método que muestra todos los pedidos
    public static void mostrarTodosLosPedidos(EntityManager em) {

        System.out.println("\n--- LISTA DE TODOS LOS PEDIDOS GUARDADOS ---");

        List<Pedido> pedidos = em.createQuery("SELECT p FROM Pedido p", Pedido.class)
                .getResultList();

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos guardados.");
            return;
        }

        for (Pedido p : pedidos) {
            System.out.println("\n" + p);
        }
    }
}
