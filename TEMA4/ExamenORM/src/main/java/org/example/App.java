package org.example;

import jakarta.persistence.*;
/**
 * @author Alvaro Lozano
 * @since 27/11/2025
 * ═══════════════════════════════════════════════════════════════════════════
 * App.Java - GESTIÓN DE PERSISTENCIA DE CLIENTES CON JPA E HIBERNATE
 * ═══════════════════════════════════════════════════════════════════════════
 */

public class App {
    // EntityManagerFactory: Fabrica que crea EntityManagers (UNA por aplicación)
    // Esto lee el fichero 'persistence.xml' y busca la unidad de persistencia "default"
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    /**
     * ═══════════════════════════════════════════════════════════════════
     * MÉTODO MAIN - PUNTO DE ENTRADA DEL PROGRAMA
     * ═══════════════════════════════════════════════════════════════════
     */
    public static void main(String[] args) {

        System.out.println("\nLos métodos de este programa están implementados por: \nAlvaro Lozano\n");
        System.out.println("=== INICIO DE LA APLICACIÓN DE GESTIÓN DE CLIENTES PARA EL EXAMEN ===\n");

        try {

            // ═════════════════════════════════════════════════════════
            // APARTADO A: INSERTAR UN NUEVO CLIENTE
            // ═════════════════════════════════════════════════════════
            InsertarNuevoCliente();

            // ═════════════════════════════════════════════════════════
            // APARTADO B: CONSULTAR UN CLIENTE EXISTENTE
            // ═════════════════════════════════════════════════════════
            ConsultarClienteExistente();

            // ═════════════════════════════════════════════════════════
            // APARTADO C: MODIFICAR UN CLIENTE EXISTENTE
            // ═════════════════════════════════════════════════════════
            ModificarClienteExistente();

            // ═════════════════════════════════════════════════════════
            // APARTADO D: ELIMINAR UN CLIENTE EXISTENTE
            // ═════════════════════════════════════════════════════════
            EliminarClienteExistente();

        } catch (Exception e) {
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerramos la factoría al finalizar la aplicación
            emf.close();
            System.out.println("\n=== FIN DE LA APLICACIÓN ===");

        }

    }

    /**
     * ═══════════════════════════════════════════════════════════════════
     * IMPLEMENTACIÓN DE LOS MÉTODOS DEL EXAMEN
     * ═══════════════════════════════════════════════════════════════════
     */

    private static void InsertarNuevoCliente() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        String clienteGenerado = null;


        try {
            tx = em.getTransaction();
            tx.begin(); //Inicio de transacción Sql

            Cliente cliente = new Cliente(); //Aquí el objeto se crea, estado de ciclo de vida NEW o TRANSIENT


            cliente.setDni("96878083K");
            cliente.setApellidos("OLIVARES");
            cliente.setCp("30510");

            em.persist(cliente); //Ahora pasa de TRANSIENT a estado MANAGED

            tx.commit(); //Con el commit todos los bojetos en estado MANAGED se sincronizan con la BBDD
            //Fin de la transacción

            clienteGenerado = cliente.getDni();
            System.out.println("Nuevo cliente generado " + clienteGenerado + " con éxito.");


        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            // Fallo → SQL ROLLBACK, se descartan INSERT/UPDATE/DELETE SQL
            System.err.println("Error: " + e.getMessage());
        } finally {
            em.close();
            // Cerrar EntityManager = Desconectar sesión ORM (objetos pasan a DETACHED si eran MANAGED)
        }


    }

    private static void ConsultarClienteExistente() {

        EntityManager em = emf.createEntityManager();

        try {
            String DniBuscar = "96878083K";

            System.out.println("1º Usando find(): ");
            Cliente cliente1 = em.find(Cliente.class, DniBuscar);

            if (cliente1 != null) {
                System.out.println("Cliente: " +
                        "\nDNI: " + cliente1.getDni() +
                        "\nApellido: " + cliente1.getApellidos() +
                        "\nCP: " + cliente1.getCp());
            } else {
                System.out.println("Cliente no encontrado (find() devuelve null si no exite");
            }


        } catch (Exception e) {
            System.out.println("Error en SELECT find(): " + e.getMessage());
        } finally {
            em.close(); // Finalizamos sesión ORM
        }
    }

    private static void ModificarClienteExistente() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();
            String DniBuscar = "96878083K";

            Cliente cliente1 = em.find(Cliente.class, DniBuscar); //Bucamos la entidad por su PK y pasamos el objeto a MANAGED

            if (cliente1 != null) {
                String cpAnterior = cliente1.getCp();
                String cpNuevo = "30520";
                cliente1.setCp(cpNuevo);
                System.out.println("Modificando el CP de cliente existente: " +
                        "\nCP anterior: "+ cpAnterior +
                        "\nCP nuevo: "+cliente1.getCp());
            } else {
                System.out.println("Cliente no encontrado");
            }

            tx.commit();//JPA/hibernate sincronizan los cambios


        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("UPDATE fallido → Rollback SQL: " + e.getMessage());
        } finally {
            em.close(); // Los objetos MANAGED pasan a DETACHED al cerrar sesión
        }
    }

    private static void EliminarClienteExistente() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            String DniBuscar = "96878083K";

            Cliente cliente1 = em.find(Cliente.class, DniBuscar);

            if (cliente1 != null) {
                em.remove(cliente1);
                System.out.println("Eliminando el cliente existente: " + cliente1.getDni());

            } else {
                System.out.println("Cliente no encontrado");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("UPDATE fallido → Rollback SQL: " + e.getMessage());
        } finally {
            em.close(); //  cerrar sesión
        }

    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////