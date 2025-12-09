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

    private static List<Producto> productosDisponibles = new ArrayList<>();

    public static void main(String[] args) {

        emf = Persistence.createEntityManagerFactory("C:\\objectdb-2.9.4\\db\\PedidosODB.odb");
        em = emf.createEntityManager();

        crearProductosIniciales();

        System.out.println("\n=== CREACIÓN DE PEDIDOS ===");
        guardarPedido();

        em.close();
        emf.close();
    }

    private static void crearProductosIniciales() {
        em.getTransaction().begin();

        Producto p1 = new Producto("Consola X", 499.99, 12);
        Producto p2 = new Producto("Juego Z", 59.99, 50);
        Producto p3 = new Producto("Teclado Mecánico", 89.99, 20);

        em.persist(p1);
        em.persist(p2);
        em.persist(p3);

        productosDisponibles.add(p1);
        productosDisponibles.add(p2);
        productosDisponibles.add(p3);

        em.getTransaction().commit();

        System.out.println("\nProductos cargados correctamente.");
    }

    private static void mostrarProductos() {
        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");

        for (int i = 0; i < productosDisponibles.size(); i++) {
            System.out.println((i + 1) + " - " + productosDisponibles.get(i));
        }
    }

    private static void guardarPedido() {

        System.out.print("\nCódigo del pedido: ");
        String codigo = sc.nextLine();

        System.out.print("ID del cliente: ");
        Long idCliente = sc.nextLong();
        sc.nextLine();

        mostrarProductos();

        List<Producto> seleccionados = new ArrayList<>();
        String opcion;

        do {
            System.out.print("\nSeleccione un número de producto (o 'fin' para terminar): ");
            opcion = sc.nextLine();

            if (!opcion.equalsIgnoreCase("fin")) {
                try {
                    int indice = Integer.parseInt(opcion) - 1;

                    if (indice >= 0 && indice < productosDisponibles.size()) {
                        seleccionados.add(productosDisponibles.get(indice));
                        System.out.println("Producto añadido.");
                    } else {
                        System.out.println("Índice no válido.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida.");
                }
            }

        } while (!opcion.equalsIgnoreCase("fin"));

        Pedido pedido = new Pedido(codigo, idCliente, seleccionados);

        em.getTransaction().begin();
        em.persist(pedido);
        em.getTransaction().commit();

        System.out.println("\nPedido guardado correctamente:");
        System.out.println(pedido);
    }
}
