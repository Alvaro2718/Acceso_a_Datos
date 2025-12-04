package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import java.util.List;

public class Main {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static void main(String[] args) {
        try {

            // INSERT PREVIO → CAPTURA 1 también puede usarse para mostrar que el registro existe
            System.out.println("\n--- INSERT previo para tener datos en consultas ---");
            CrearNuevaLlamada();

            // CONSULTA 1 SQL NATIVO → Campos sueltos → devuelve Object[]
            System.out.println("\n--- APARTADO 1: Consulta Simple con SQL Nativo ---");
            consultaSimpleSQL();

            // CONSULTA 2 SQL NATIVO → Filtrado por DURACIÓN
            System.out.println("\n--- APARTADO 2: Consulta Filtrada con SQL Nativo ---");
            consultaFiltradaSQL();

            // CONSULTA 3 JPQL → Recibe entidades directamente → navegación a FK como objeto
            System.out.println("\n--- APARTADO 3: Consulta Simple con JPQL ---");
            consultaSimpleJPQL();

            // CONSULTA 4 JPQL → Condición WHERE sobre atributo (no columna)
            System.out.println("\n--- APARTADO 4: Consulta con condición (importe < 300) ---");
            consultaImporteMenor300JPQL();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

    // ===========================================
    //  MÉTODO INSERT → persist() genera INSERT SQL
    // ===========================================
    public static Integer CrearNuevaLlamada() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Integer codigoGenerado = null;

        try {
            tx.begin();

            // Creamos objeto TRANSIENT
            LlamadasEmitida nuevaLlamada = new LlamadasEmitida();
            int simLlamante = 617478396;

            // Buscamos FK → find() hace SELECT SQL inmediato
            TarjetasTelefonica tarjeta = em.find(TarjetasTelefonica.class, simLlamante);

            if (tarjeta == null) {
                System.out.println("La tarjeta " + simLlamante + " no existe. Creándola...");
                tarjeta = new TarjetasTelefonica();
                tarjeta.setId(simLlamante);

                Agente agente = em.find(Agente.class, 9);
                if (agente == null) {
                    System.out.println("Agente 9 no existe. Creando Agente Demo...");
                    agente = new Agente();
                    agente.setId(9);
                    agente.setNombreAgente("Agente Demo");
                    agente.setFraseClave("Clave123");
                    em.persist(agente);
                }

                tarjeta.setCodigoAgenteAsociado(agente);
                em.persist(tarjeta);
            }

            // Asignamos FK como objeto MANAGED
            nuevaLlamada.setSimLlamante(tarjeta);

            // Mapeo de atributos → columnas SQL
            nuevaLlamada.setNumeroLlamado(987654321);
            nuevaLlamada.setDuracionLlamada(350);
            nuevaLlamada.setImporteLlamada(45.75f);

            em.persist(nuevaLlamada); // TRANSIENT → MANAGED → INSERT SQL en commit()

            tx.commit();
            codigoGenerado = nuevaLlamada.getId();
            System.out.println("Llamada creada correctamente. ID = " + codigoGenerado);

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.out.println("Error INSERT: " + e.getMessage());
        } finally {
            em.close();
        }

        return codigoGenerado;
    }

    // ===========================================
    //  CONSULTA 1: SQL NATIVO (campos sueltos)
    // ===========================================
    private static void consultaSimpleSQL() {
        EntityManager em = emf.createEntityManager();
        System.out.println("\nListado de llamadas (SIM, Número, Importe):\n");

        try {
            // Query directa a tabla/columnas reales → SQL se envía a BBDD (ej: :contentReference[oaicite:0]{index=0} o similar)
            String sql = "SELECT SIM_LLAMANTE, NUMERO_LLAMADO, IMPORTE_LLAMADA FROM LLAMADAS_EMITIDAS";
            Query query = em.createNativeQuery(sql);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No hay llamadas.");
                return;
            }

            for (Object[] fila : resultados) {
                System.out.printf("| %-12d | %-10d | %6.2f € |%n", fila[0], fila[1], fila[2]);
            }

            System.out.println("\nTotal: " + resultados.size());

        } catch (Exception e) {
            System.out.println("Error SQL simple: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ===========================================
    //  CONSULTA 2: SQL NATIVO (duración > 300)
    // ===========================================
    private static void consultaFiltradaSQL() {
        EntityManager em = emf.createEntityManager();
        System.out.println("\nLlamadas con duración > 300 seg:\n");

        try {
            String sql = "SELECT * FROM LLAMADAS_EMITIDAS WHERE DURACION_LLAMADA > ?";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, 300);

            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No hay llamadas largas.");
                return;
            }

            for (Object[] fila : resultados) {
                System.out.printf("| ID: %-8d | SIM: %-10d | Duración: %-4d | Importe: %6.2f € |%n",
                        fila[0], fila[1], fila[3], fila[4]);
            }

            System.out.println("\nTotal filtrados: " + resultados.size());

        } catch (Exception e) {
            System.out.println("Error SQL filtrado: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ===========================================
    //  CONSULTA 3: JPQL simple → entidades
    // ===========================================
    private static void consultaSimpleJPQL() {
        EntityManager em = emf.createEntityManager();
        System.out.println("\nListado de llamadas con JPQL:\n");

        try {
            // JPQL trabaja sobre ENTIDADES y atributos → portable a cualquier JPA Provider (ej: :contentReference[oaicite:1]{index=1})
            String jpql = "SELECT l FROM LlamadasEmitida l";
            Query query = em.createQuery(jpql, LlamadasEmitida.class);

            List<LlamadasEmitida> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No hay llamadas.");
                return;
            }

            for (LlamadasEmitida llamada : resultados) {
                System.out.printf("| ID: %-8d | SIM: %-10d | Número: %-10d | Duración: %-4d | Importe: %6.2f € |%n",
                        llamada.getId(),
                        llamada.getSimLlamante().getId(),  // Navegamos el objeto FK
                        llamada.getNumeroLlamado(),
                        llamada.getDuracionLlamada(),
                        llamada.getImporteLlamada());
            }

            System.out.println("\nTotal: " + resultados.size());

        } catch (Exception e) {
            System.out.println("Error JPQL simple: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ==================================================
    //  CONSULTA 4: JPQL con condición → WHERE atributo
    // ==================================================
    private static void consultaImporteMenor300JPQL() {
        EntityManager em = emf.createEntityManager();
        System.out.println("\nLlamadas con importe < 300 €:\n");

        try {
            String jpql = "SELECT l FROM LlamadasEmitida l WHERE l.importeLlamada < 300";
            Query query = em.createQuery(jpql, LlamadasEmitida.class);

            List<LlamadasEmitida> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                System.out.println("No hay llamadas baratas.");
                return;
            }

            for (LlamadasEmitida llamada : resultados) {
                System.out.printf("| ID: %-8d | SIM: %-10d | Importe: %6.2f € |%n",
                        llamada.getId(),
                        llamada.getSimLlamante().getId(),
                        llamada.getImporteLlamada());
            }

            System.out.println("\nTotal: " + resultados.size());

        } catch (Exception e) {
            System.out.println("Error JPQL importe: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
