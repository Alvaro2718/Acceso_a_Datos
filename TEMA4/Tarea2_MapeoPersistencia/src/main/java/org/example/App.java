package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class App {
    // 1º La SessionFactory es "pesada", se crea una sola vez.
    private static final SessionFactory sessionFactory = buildSessionFactory();

    public static void main(String[] args) {
        // 1. Insertamos un nuevo registro
        registroNuevo(1000072);

        // 2. Modificamos un registro existente
        modificarRegistro(1000071);

        // 3. Consultamos todas las llamadas
        consultaHQL();

        // 4. Cerramos la SessionFactory
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("SessionFactory cerrada.");
        }
    }

    // Inicialización de la SessionFactory
    private static SessionFactory buildSessionFactory() {
        System.out.println("Creando la SessionFactory (esto solo debe pasar UNA VEZ)...");
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Insertar un nuevo registro
    public static void registroNuevo(int nuevoCodigoLlamada) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaccion = null;
            try {
                transaccion = session.beginTransaction();



                // Creamos la nueva llamada
                LlamadasEmitida llamada = new LlamadasEmitida();
                llamada.setCodigoLlamada(nuevoCodigoLlamada);
                llamada.setNumeroLlamado(481125894);
                llamada.setDuracionLlamada(33);
                llamada.setImporteLlamada(3.4f);
                llamada.setSimLlamante(617478396);

                session.persist(llamada);
                transaccion.commit();

                System.out.println("Registro insertado con éxito (ID: " + nuevoCodigoLlamada + ")");

            } catch (Exception e) {
                if (transaccion != null) transaccion.rollback();
                System.err.println("Error al insertar el registro: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Modificar un registro existente
    public static void modificarRegistro(int codigoLlamada) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaccion = null;
            try {
                transaccion = session.beginTransaction();

                LlamadasEmitida llamada = session.find(LlamadasEmitida.class, codigoLlamada);

                if (llamada != null) {
                    System.out.println("Modificando llamada " + codigoLlamada +
                            ". Duración actual: " + llamada.getDuracionLlamada());
                    llamada.setDuracionLlamada(20);
                    llamada.setImporteLlamada(3.5f);

                    session.persist(llamada);
                    transaccion.commit();
                    System.out.println("Registro modificado con éxito");
                } else {
                    System.out.println("No se encontró el registro con el código: " + codigoLlamada);
                }
            } catch (Exception e) {
                if (transaccion != null) transaccion.rollback();
                System.err.println("Error al modificar el registro: " + e.getMessage());
            }
        }
    }

    // Consultar todos los registros
    public static void consultaHQL() {
        try (Session session = sessionFactory.openSession()) {
            List<LlamadasEmitida> llamadas = session.createQuery("FROM LlamadasEmitida", LlamadasEmitida.class).list();

            System.out.println("--- RESULTADO DE LA CONSULTA HQL ---");
            for (LlamadasEmitida llamada : llamadas) {
                System.out.println("CODIGO DE LLAMADA: " + llamada.getCodigoLlamada() +
                        ", NUMERO LLAMADO: " + llamada.getNumeroLlamado());
            }
        } catch (Exception e) {
            System.err.println("Error al consultar: " + e.getMessage());
        }
    }
}
