package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

// ================================================
// MAPEADO RELACIONAL <--> OBJETOS USANDO HIBERNATE
// ================================================

public class App {

    // SessionFactory es un objeto costoso de crear: representa la conexión/configuración con Hibernate.
    // Debe inicializarse UNA SOLA VEZ y reutilizarse durante toda la ejecución del programa.
    private static final SessionFactory sessionFactory = buildSessionFactory();

    public static void main(String[] args) {
        // 1. Creamos un OBJETO Java y lo guardamos en la BD (mapeo → INSERT SQL)
        registroNuevo(1000072);

        // 2. Recuperamos un OBJETO desde la BD por su PK y cambiamos sus datos (mapeo → UPDATE SQL)
        modificarRegistro(1000071);

        // 3. Consultamos todas las INSTANCIAS/OBJETOS de la clase mediante HQL (mapeo → SELECT SQL)
        consultaHQL();

        // 4. Cerramos la SessionFactory cuando ya no la necesitemos para liberar recursos
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("SessionFactory cerrada correctamente.");
        }
    }

    // Método auxiliar que inicializa Hibernate leyendo hibernate.cfg.xml
    private static SessionFactory buildSessionFactory() {
        System.out.println("Inicializando SessionFactory (solo ocurre 1 vez)...");
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Fallo en la configuración de Hibernate: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // -------------------------------------------------------------------
    // INSERT → Guardamos un objeto Java como nuevo registro en la BD
    // -------------------------------------------------------------------
    public static void registroNuevo(int nuevoCodigoLlamada) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaccion = null;

            try {
                transaccion = session.beginTransaction();

                // Creamos la nueva llamada como OBJETO Java
                // Cada atributo mapeado en la clase equivale a una columna en la tabla relacional
                LlamadasEmitida llamada = new LlamadasEmitida();

                llamada.setCodigoLlamada(nuevoCodigoLlamada); // PK (clave primaria)
                llamada.setNumeroLlamado(481125894);         // columna de la tabla
                llamada.setDuracionLlamada(33);              // columna de la tabla
                llamada.setImporteLlamada(3.4f);             // columna de la tabla
                llamada.setSimLlamante(617478396);           // columna de la tabla

                // Guardamos el objeto: Hibernate lo traduce a INSERT SQL
                session.persist(llamada);
                transaccion.commit();

                System.out.println("Objeto insertado en BD (ID/PK: " + nuevoCodigoLlamada + ")");

            } catch (Exception e) {
                if (transaccion != null) transaccion.rollback();
                System.err.println("Error en el INSERT: " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------
    // UPDATE → Buscamos un objeto por su PK y modificamos sus atributos
    // -------------------------------------------------------------------
    public static void modificarRegistro(int codigoLlamada) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaccion = null;

            try {
                transaccion = session.beginTransaction();

                // Hibernate recupera el registro como OBJETO Java usando su clave primaria
                LlamadasEmitida llamada = session.find(LlamadasEmitida.class, codigoLlamada);

                if (llamada != null) {
                    System.out.println("Objeto encontrado. Actualizando campos...");

                    llamada.setDuracionLlamada(20);   // se mapeará a columna BD
                    llamada.setImporteLlamada(3.5f);  // se mapeará a columna BD

                    // session.persist() también sirve para re-guardar el objeto modificado
                    session.persist(llamada);

                    // Hibernate lo convierte en UPDATE SQL al hacer commit()
                    transaccion.commit();
                    System.out.println("Objeto actualizado en BD (PK: " + codigoLlamada + ")");

                } else {
                    System.out.println("No existe ningún objeto/registro con esa PK.");
                }

            } catch (Exception e) {
                if (transaccion != null) transaccion.rollback();
                System.err.println("Error en el UPDATE: " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------
    // SELECT (HQL) → Consultamos objetos usando un lenguaje orientado a CLASES
    // -------------------------------------------------------------------
    public static void consultaHQL() {
        try (Session session = sessionFactory.openSession()) {

            // HQL consulta sobre la clase mapeada, no sobre una tabla directamente
            List<LlamadasEmitida> llamadas =
                    session.createQuery("FROM LlamadasEmitida", LlamadasEmitida.class).list();

            System.out.println("--- LISTADO DE OBJETOS RECUPERADOS CON HQL ---");
            for (LlamadasEmitida llamada : llamadas) {

                // Accedemos a los atributos del objeto (mapeados desde columnas SQL)
                System.out.println("PK: " + llamada.getCodigoLlamada() +
                        ", Número: " + llamada.getNumeroLlamado());
            }

        } catch (Exception e) {
            System.err.println("Error en el SELECT HQL: " + e.getMessage());
        }
    }
}