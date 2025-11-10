package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;


public class MainNativo {
    public static void main(String[] args) {

        System.out.println("Intentando conectar (Modo Nativo de Hibernate)...");

        SessionFactory sessionFactory = null;
        try {
            // 1. Crea un objeto Configuration
            Configuration config = new Configuration();

            // 2. Carga el fichero de configuración nativo
            //    (busca "hibernate.cfg.xml" en resources)
            config.configure("hibernate.cfg.xml");

            // 3. Construye la "Fábrica de Sesiones" (el equivalente a EntityManagerFactory)
            sessionFactory = config.buildSessionFactory();

            // Si llega aquí, la configuración fue exitosa.
            System.out.println("¡CONEXIÓN EXITOSA!");
            System.out.println("SessionFactory creada: " + sessionFactory.getClass().getName());

            // Opcional: Podemos probar a abrir una sesión (el equivalente a un EntityManager)
            Session session = sessionFactory.openSession();
            System.out.println("¡Sesión de Hibernate abierta y cerrada!");
            session.close();

        } catch (Exception e) {
            // Si falla (no encuentra el XML, error de BBDD, mal dialecto...)
            System.err.println("ERROR AL CONFIGURAR HIBERNATE:");
            e.printStackTrace();

        } finally {
            // Cierra el factory si se llegó a crear.
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
                System.out.println("SessionFactory cerrada.");
            }
        }
    }
}

