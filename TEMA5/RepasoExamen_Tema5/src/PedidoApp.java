import jakarta.persistence.*;

import java.util.*;


public class PedidoApp {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("C:/objectdb-2.9.4/dbInventarioODB");
    static EntityManager em = emf.createEntityManager();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int opcion;
        try {

            do {
                mostraMenu();
                opcion = sc.nextInt();
                sc.nextLine();
                switch (opcion) {

                    case 1:
                        crearPedido();
                        break;
                    case 2:
                        anadirProducto();
                        break;
                    case 3:
                        eliminarProducto();
                        break;
                    case 4:
                        mostrarPedido();
                        break;
                    case 5:
                        mostrarProducto();
                        break;
                    case 6:
                        productosStockBajo();
                        break;
                    case 0:
                        System.out.println("Saliendo del programa....Adiós");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }


            } while (opcion != 0);

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        em.close();
        emf.close();
    }

    // --- MÉTODOS PARA EL PROGRAMA

    /// /////////1º Mostrar el Menú
    static void mostraMenu() {

        System.out.println(" \n ---Menú Pedidos---");
        System.out.println("1. Crear Pedido");
        System.out.println("2. Añadir Producto");
        System.out.println("3. Eliminar Producto");
        System.out.println("4. Mostrar Pedido");
        System.out.println("5. Mostrar Producto");
        System.out.println("6. Mostrar Productos con stock menor de 20");
        System.out.println("0. Salir del programa");
        System.out.println("Elíje una opción: ");

    }

    /// ////////1º Crear un Pedido
    static void crearPedido() {

        System.out.println("Código pedido: ");
        String codigo = sc.nextLine();

        System.out.println("ID del Cliente: ");
        Long idCliente = Long.parseLong(sc.nextLine());
        sc.nextLine();

        Pedido pedido = new Pedido(codigo, idCliente, new ArrayList<>());

        em.getTransaction().begin();
        em.persist(pedido);
        em.getTransaction().commit();

        System.out.println("Pedido creado.");
    }

    /// ///////2º Añadir producto
    static void anadirProducto() {

        System.out.print("Id pedido: ");
        Long idPedido = sc.nextLong();
        //Ejemplo de transformar un tipo String a un tipo Long
        //Long idPedido = Long.parseLong(sc.nextLine());
        sc.nextLine();

        Pedido pedido = em.find(Pedido.class, idPedido);
        if (pedido == null) {
            System.out.println("Pedido no encontrado.");
            return;
        }

        System.out.println("Nombre del producto: ");
        String nombre = sc.nextLine();

        System.out.println("Precio: ");
        double precio = sc.nextDouble();
        //Ejemplo de transformar un tipo de dato String a double
        //double precio = Double.parseDouble(sc.nextLine());

        System.out.println("Stock: ");
        int stock = sc.nextInt();
        //Ejemplo de tranformar un tipo de dato String a int
        //int stock = Integer.parseInt(sc.nextLine());

        Producto p = new Producto(nombre, precio, stock);

        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        System.out.println("Producto creado.");

    }

    /// ///// 3. ELIMINAR PRODUCTO DE UN PEDIDO
    static void eliminarProducto() {
        System.out.print("ID pedido: ");
        Long idPedido = sc.nextLong();

        System.out.print("ID producto: ");
        Long idProducto = sc.nextLong();
        sc.nextLine();

        Pedido pedido = em.find(Pedido.class, idPedido);
        if (pedido == null) {

            System.out.println("Pedido no encontrado.");
            return;
        }
        em.getTransaction().begin();
        pedido.getProductos().removeIf(p -> p.getId().equals(idProducto));
        em.getTransaction().commit();

        System.out.println("Producto Eliminado.");
    }

    ///  ////4. MOSTRAR PEDIDO
    static void mostrarPedido() {
        System.out.print("Id pedido: ");
        Long idPedido = sc.nextLong();
        sc.nextLine();

        Pedido pedido = em.find(Pedido.class, idPedido);
        if (pedido == null) {
            System.out.println("Pedido no encontrado.");
            return;
        }

        System.out.println("Pedido: " + pedido.getCodigo());
        for (Producto p : pedido.getProductos()) {
            System.out.println(p.getNombre());
            System.out.println(p.getPrecio());

        }
    }

    /// ////5. MOSTRAR PRODUCTO
    static void mostrarProducto() {
        System.out.print("ID producto: ");
        Long id = sc.nextLong();
        sc.nextLine();
        Producto p = em.find(Producto.class, id);
        if (p == null) {
            System.out.println("Producto no encontrado.");
        } else {
            System.out.println(p.getNombre() + " - " + p.getPrecio() + " /  " + p.getStock());
        }
    }

    static void productosStockBajo() {
        List<Producto> productos = em.createQuery(
                "SELECT p FROM Producto p WHERE p.stock < 20",
                Producto.class
        ).getResultList();

        productos.forEach(p ->
                System.out.println(p.getNombre() + " (" + p.getStock() + ")"));
    }


}
