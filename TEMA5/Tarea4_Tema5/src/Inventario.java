import jakarta.persistence.*;
import java.util.List;

public class Inventario {

    public static void main(String[] args) {

        // Creamos la conexión con la base de datos ObjectDB.
        // Aquí indicamos la ruta del archivo .odb, que es donde está guardada toda la información
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory(
                        "C:/Users/AlumnoDAM2/Desktop/Acceso a Datos(Loren)/Tema 5/Tarea4/InventarioCompleto.odb");

        // El EntityManager lo usamos para hacer consultas y modificaciones
        EntityManager em = emf.createEntityManager();

        try {

            // --- CONSULTA DE PRODUCTOS CON STOCK BAJO ---

            // Mostramos por pantalla qué vamos a consultar
            System.out.println("Productos con stock menor a 20:");

            // Creamos una consulta JPQL para buscar productos con stock inferior a 20
            // No se consulta una tabla, sino la clase Producto
            TypedQuery<Producto> consultaStock =
                    em.createQuery(
                            "SELECT p FROM Producto p WHERE p.stock < 20",
                            Producto.class);


            // Ejecutamos la consulta y guardamos el resultado en una lista
            List<Producto> productosBajoStock = consultaStock.getResultList();

            // Recorremos la lista y mostramos cada producto por consola
            for (Producto p : productosBajoStock) {
                System.out.println(p);
            }


            // ACTUALIZAR EL PRECIO DE UN PRODUCTO

            // Iniciamos una transacción porque vamos a modificar datos
            em.getTransaction().begin();

            // Buscamos el producto con ID 5
            Producto productoPrecio = em.find(Producto.class, 5);
            System.out.println("Producto antes de modificar: " + productoPrecio);

            // Si el producto existe, modificamos su precio
            if (productoPrecio != null) {
                productoPrecio.setPrecio(140.0);
                System.out.println("\nPrecio actualizado del producto ID 5"+ productoPrecio);
            } else {
                // Si no se encuentra el producto, lo indicamos por pantalla
                System.out.println("\nProducto ID 5 no encontrado");
            }

            // Confirmamos la transacción para guardar los cambios
            em.getTransaction().commit();


            // ACTUALIZAR EL STOCK DE UN PRODUCTO

            // Volvemos a iniciar una transacción para otra modificación
            em.getTransaction().begin();

            // Buscamos el producto con ID 17
            Producto productoStock = em.find(Producto.class, 17);
            System.out.println("Producto stock: " + productoStock);

            // Si el producto existe, reducimos su stock en tres unidades
            if (productoStock != null) {
                productoStock.setStock(productoStock.getStock() - 3);
                System.out.println("Stock actualizado del producto ID 17: " + productoStock);
            } else {
                // Si no existe, mostramos un mensaje
                System.out.println("Producto ID 17 no encontrado");
            }

            // Guardamos definitivamente los cambios
            em.getTransaction().commit();

        } catch (Exception e) {

            // Si ocurre algún error, mostramos la excepción
            e.printStackTrace();

            // Si la transacción estaba activa, la cancelamos
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

        } finally {

            // Cerramos el EntityManager y el EntityManagerFactory
            // para liberar los recursos utilizados
            em.close();
            emf.close();
        }
    }
}
