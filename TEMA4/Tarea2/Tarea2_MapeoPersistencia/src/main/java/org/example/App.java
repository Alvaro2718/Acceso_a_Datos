package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * @author Alvaro Lozano
 * @since 13/11/2025
 *
 */
public class App {
    //1º La SessionFactory es un objeto "pesado" y costoso de crear.
    // Solo se debe crear UNA VEZ cuando arranca la aplicación.
    private static final SessionFactory sessionFactory = buildSessionFactory();


    public static void main(String[] args) {

        // 1. Insertamos un nuevo registro con el ID 1000072
        registroNuevo(1000072);


        // 2. Modificamos el registro con ID 1000071 (que ya debería existir)
        modificarRegistro(1000071);

        // 3. Consultamos todas las llamadas para ver nuestros cambios
        consultaHQL();


        // 4. Al terminar toda la aplicación, cerramos la fábrica (sesión).
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("SessionFactory cerrada.");
        }


    }

    /**
     * Método que inicializa la SessionFactory una única vez.
     */
    private static SessionFactory buildSessionFactory() {
        System.out.println("Creando la SessionFactory (esto solo debe pasar UNA VEZ)...");
        try {
            // Carga la configuración (hibernate.cfg.xml) y construye la fábrica.
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Si falla la creación (ej. no encuentra el XML o no conecta a la BD),
            // es un error fatal que debe parar la aplicación.
            System.err.println("Error al crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Método para insertar un nuevo registro en la tabla LLAMADAS_EMITIDAS.
     *
     * @param nuevoCodigoLlamada El ID que asignaremos manualmente.
     */
    public static void registroNuevo(int nuevoCodigoLlamada) {
        // Obtenemos una sesión de la fábrica (esto es rápido).
        // Usamos "try-with-resources" para que la sesión se cierre sola al final.
        try (Session session = sessionFactory.openSession()) {
            Transaction transaccion = null; // Inicializamos la transacción
            try {
                // ¡Empezamos la transacción!
                transaccion = session.beginTransaction();

                // Creamos la nueva llamada (un objeto Java en memoria)
                LlamadasEmitida llamada = new LlamadasEmitida();

                // Le asignamos un ID manualmente.
                llamada.setCodigoLlamada(nuevoCodigoLlamada);
                // Asignamos el resto de valores
                llamada.setNumeroLlamado(481125894);
                llamada.setDuracionLlamada(33);
                llamada.setImporteLlamada(3.4f);

                // "session.persist(llamada)" le dice a Hibernate: "prepárate para guardar este objeto".
                // Todavía no ejecuta el INSERT.
                session.persist(llamada);

                // "transaccion.commit()" es el momento en que Hibernate revisa los cambios
                // y ejecuta el INSERT real en la BBDD.
                transaccion.commit();
                System.out.println("Registro insertado con éxito (ID: " + nuevoCodigoLlamada + ")");


            } catch (Exception e) {
                // Si algo falla (ej. el ID ya existe), revertimos la transacción.
                if (transaccion != null) {
                    transaccion.rollback();
                }
                System.err.println("Error al insertar el registro: " + e.getMessage());
                e.printStackTrace();
            }


        }// <-- Gracias a try-with-resources, session.close() se llama aquí automáticamente.

    }

    /**
     * Método para modificar un registro existente.
     *
     * @param codigoLlamada El ID del registro a modificar.
     */
    public static void modificarRegistro(int codigoLlamada) {
        // Obtenemos una sesión de la fábrica
        try (Session session = sessionFactory.openSession()) {
            Transaction transaccion = null;
            try {
                transaccion = session.beginTransaction();

                // 1. OBTENER el objeto
                // "session.get" busca la llamada en la BBDD y la trae a memoria.
                // El objeto "llamada" queda en estado "Persistente".
                LlamadasEmitida llamada = session.find(LlamadasEmitida.class, codigoLlamada);

                if (llamada != null) {
                    // 2. MODIFICAR el objeto (solo en memoria)
                    System.out.println("Modificando llamada " + codigoLlamada + ". Duración actual: " + llamada.getDuracionLlamada());
                    llamada.setDuracionLlamada(20);
                    llamada.setImporteLlamada(3.5f);

                    // 3. GUARDAR
                    // "session.persist(llamada)" le avisa a Hibernate que el objeto ha cambiado.
                    session.persist(llamada);

                    // "transaccion.commit()" detecta los cambios en objetos "Persistentes"
                    // y ejecuta el UPDATE automáticamente.
                    transaccion.commit();
                    System.out.println("Registro modificado con éxito");
                } else {
                    System.out.println("No se encontró el registro con el código: " + codigoLlamada);
                }
            } catch (Exception e) {
                if (transaccion != null) {
                    transaccion.rollback();
                }
                System.err.println("Error al modificar el registro: " + e.getMessage());
            }
        }
    }

    /**
     * Método que consulta TODOS los registros usando HQL.
     */
    public static void consultaHQL() {
        // Obtenemos una sesión de la fábrica
        try (Session session = sessionFactory.openSession()) {

            // HQL (Hibernate Query Language) es "casi" SQL pero orientado a OBJETOS.
            // No seleccionamos de la "tabla" LLAMADAS_EMITIDAS...
            // ...sino de la "clase Java" (el POJO) llamada "LlamadasEmitida".
            List<LlamadasEmitida> llamadas = session.createQuery("FROM LlamadasEmitida", LlamadasEmitida.class).list(); // ".list()" ejecuta la consulta y trae los resultados

            System.out.println("--- RESULTADO DE LA CONSULTA HQL ---");
            for (LlamadasEmitida llamada : llamadas) {
                // --- ¡ERROR CORREGIDO! ---
                // Tu `println` original estaba mal.
                // El código de llamada se obtiene con "getId()".
                System.out.println("CODIGO DE LLAMADA: " + llamada.getCodigoLlamada() +
                        ", NUMERO LLAMADO: " + llamada.getNumeroLlamado());
            }
        } catch (Exception e) {
            System.err.println("Error al consultar: " + e.getMessage());
        }
    }
}


/**
 * Método que consulta TODOS los registros usando HQL.
 */