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
            System.out.println("\n--- APARTADO 3: Consulta simple con JPQL Nativo --- ");
            consultaSimpleJPQL();

            // CONSULTA FILTRADA JPQL





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
            String sql = "SELECT * FROM LLAMADAS_EMITIDAS";
            Query query = em.createNativeQuery(sql);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No se encontraron llamadas emitidas.");
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
    private static void consultaSimpleJPQL(){
        EntityManager em = emf.createEntityManager();
        System.out.println("\nListado de TODAS las llamadas emitidas (USANDO JPQL):\n" );

        try {
            String jsql = "SELECT l FROM LlamadasEmitida l";
            Query query = em.createNativeQuery(jsql);

            //Paso 2: Ejecutar la consulta y obtener resultados
            List<LlamadasEmitida> resultados = query.getResultList();

            //Paso 3: Verificar si hay resultados
            if(resultados.isEmpty()){
                System.out.println("No se encontro llamadas en la base de datos.");
                return;
            }

            //Paso 5: Iterar sobre cada objeto LlamadasEmitidas
            for(LlamadasEmitida llamada : resultados){
                int codigoLlamada = llamada.getId();
                //int simLlamante =  llamada.getSimLlamante();
                int numeroLlamado =  llamada.getNumeroLlamado();
                int duracion = llamada.getDuracionLlamada();
                float importe =  llamada.getImporteLlamada();

                System.out.printf(
                        "| %-8d | %-13d | %-8d | %8.2f € |%n",
                        codigoLlamada, numeroLlamado, duracion, importe);
            }
        }catch (Exception e){
            System.err.println("Error al ejecutar la consulta filtrada: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }


    /// ////////// METODO 4: CONSULTA FILTRADA CON JPQL


}
