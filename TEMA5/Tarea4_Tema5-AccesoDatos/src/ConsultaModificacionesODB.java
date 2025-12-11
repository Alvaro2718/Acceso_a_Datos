import jakarta.persistence.*;
import java.util.*;

public class ConsultaModificacionesODB {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(
                "C:/Users/AlumnoDAM2/Desktop/Acceso a Datos(Loren)/Tema 5/Tarea4/InventarioCompleto.odb"
        );
        EntityManager em = emf.createEntityManager();
        Scanner sc = new Scanner(System.in);

        int opcion;

        //Menú del programa
        do {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Consultar producto con stock más bajo.");
            System.out.println("2. Actualizar el precio de un producto.");
            System.out.println("3. Actualizar el stock de un producto.");
            System.out.println("4. Salir.");
            System.out.print("Opción: ");

            opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1:
                    consultarProducto(em);
                    break;

                case 2:
                    // Aquí iría el método para actualizar precio
                    System.out.println("Actualizar precio (pendiente)...");
                    break;

                case 3:
                    // Aquí iría el método para actualizar stock
                    System.out.println("Actualizar stock (pendiente)...");
                    break;

                case 4:
                    System.out.println("Saliendo del programa... Adiós.");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 4);

        //Cerrar conexiones
        em.close();
        emf.close();
        System.out.println("Conexión cerrada. Fin del programa.");
    }


    // ----------------------------------------------------------
    // MÉTODO: Consultar el producto con el stock más bajo
    // ----------------------------------------------------------
    public static void consultarProducto(EntityManager em) {

        System.out.println("\n--- PRODUCTO CON STOCK MÁS BAJO ---");

        try {
            List <Producto> producto = em.createQuery(
                            "SELECT p FROM Producto p WHERE p.stock <20",
                            Producto.class
                    ).getResultList();

            System.out.println(producto);

        } catch (NoResultException e) {
            System.out.println("No hay productos en la base de datos.");
        }
    }

}
