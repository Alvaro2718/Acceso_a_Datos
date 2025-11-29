package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.util.List;

public class GestionLlamadas {

    // OJO: debe coincidir exactamente con persistence.xml
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static void main(String[] args) {

        try {

            // CONSULTA SIMPLE
            System.out.println("--- APARTADO 1: Consulta Simple con SQL Nativo ---");
            consultaSimpleSQL();

            // CONSULTA FILTRADA
            System.out.println("\n--- APARTADO 2: Consulta Filtrada con SQL Nativo ---");
            consultaFiltradaSQL();


            // CONSULTA SIMPLE JPQL
            System.out.println("\n--- APARTADO 3: Consulta simple con JPQL --- ");
            consultaSimpleJPQL();

            // CONSULTA FILTRADA JPQL
            System.out.println("\n--- APARTADO 4: Consulta Filtrada con JPQL --- ");
            consultaFiltradaJPQL();






        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }


    ///////// MÉTODO  1: CONSULTA SIMPLE SQL

    private static void consultaSimpleSQL() {

        EntityManager em = emf.createEntityManager();
        System.out.println("\nListado de TODAS las llamadas emitidas:\n");

        try {
            String sql = "SELECT SIM_LLAMANTE, NUMERO_LLAMADO, IMPORTE_LLAMADA \nFROM LLAMADAS_EMITIDAS";
            Query query = em.createNativeQuery(sql);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No se encontraron llamadas emitidas.");
                return;
            }

            for (Object[] fila : resultados) {
                int numeroSIM = (Integer) fila[0];
                int numeroLlamado = (Integer) fila[1];
                float importe = (Float) fila[2];

                System.out.printf("| %-12d | %-13d | %8.2f € |%n", numeroSIM, numeroLlamado, importe);
            }

            System.out.println("\nTotal de registros: " + resultados.size());

        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    ////////// METODO 2: CONSULTA FILTRADA CON SQL

    private static void consultaFiltradaSQL() {

        EntityManager em = emf.createEntityManager();

        int duracionMinima = 300; // segundos
        System.out.println("\nLlamadas con duración superior a " + duracionMinima + " segundos:\n");

        try {
            String sql = "SELECT * FROM LLAMADAS_EMITIDAS WHERE DURACION_LLAMADA > ?";
            Query query = em.createNativeQuery(sql);

            query.setParameter(1, duracionMinima);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No hay llamadas con duración superior a " + duracionMinima);
                return;
            }

            for (Object[] fila : resultados) {
                int codigoLlamada = (Integer) fila[0];
                int simLlamante = (Integer) fila[1];
                int numeroLlamado = (Integer) fila[2];
                int duracion = (Integer) fila[3];
                float importe = (Float) fila[4];

                System.out.printf(
                        "| %-8d | %-12d | %-13d | %-8d | %8.2f € |%n",
                        codigoLlamada, simLlamante, numeroLlamado, duracion, importe
                );
            }

            System.out.println("\nTotal de registros filtrados: " + resultados.size());

        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta filtrada: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /// /////// METODO APARTADO 3: CONSULTA SIMPLE JPQL
    private static void consultaSimpleJPQL() {
    EntityManager em = emf.createEntityManager();
    System.out.println("\nListado de TODAS las llamadas emitidas (USANDO JPQL):\n");

    try {
        // JPQL usa el nombre de la entidad/clase, NO el nombre de la tabla
        String jpql = "SELECT l FROM LlamadasEmitida l";

        // Se usa createQuery(), NO createNativeQuery()
        Query query = em.createQuery(jpql, LlamadasEmitida.class);

        // Recibimos entidades, no Object[]
        List<LlamadasEmitida> resultados = query.getResultList();

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron llamadas.");
            return;
        }

        for (LlamadasEmitida llamada : resultados) {
            int codigoLlamada = llamada.getId();
            // Profesor quiere ver navegación a la FK como objeto:
            int numeroSIM = llamada.getSimLlamante().getId();
            int numeroLlamado = llamada.getNumeroLlamado();
            int duracion = llamada.getDuracionLlamada();
            float importe = llamada.getImporteLlamada();

            System.out.printf(
                    "| %-8d | %-12d | %-13d | %-8d | %8.2f € |%n",
                    codigoLlamada, numeroSIM, numeroLlamado, duracion, importe
            );
        }

        System.out.println("\nTotal de registros: " + resultados.size());

    } catch (Exception e) {
        System.err.println("Error en JPQL: " + e.getMessage());
    } finally {
        em.close();
    }
}


    /// ////////// METODO 4: CONSULTA FILTRADA CON JPQL
    private static void consultaFiltradaJPQL() {
        EntityManager em = emf.createEntityManager();
        System.out.println("\nLlamadas con importe MENOR a 300 € (USANDO JPQL):\n");

        try {
            String jpql = "SELECT l FROM LlamadasEmitida l WHERE l.importeLlamada < 300";
            Query query = em.createQuery(jpql, LlamadasEmitida.class);

            List<LlamadasEmitida> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No hay llamadas con importe menor a 300 €.");
                return;
            }

            for (LlamadasEmitida llamada : resultados) {
                System.out.printf(
                        "| Llamada: %-5d | SIM: %-10d | Número: %-10d | Importe: %6.2f € |%n",
                        llamada.getId(),
                        llamada.getSimLlamante().getId(),
                        llamada.getNumeroLlamado(),
                        llamada.getImporteLlamada()
                );
            }

            System.out.println("\nTotal de llamadas filtradas: " + resultados.size());

        } catch (Exception e) {
            System.err.println("Error en JPQL filtrado: " + e.getMessage());
        } finally {
            em.close();
        }
    }


}
