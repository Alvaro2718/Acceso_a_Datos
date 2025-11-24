package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityNotFoundException;

/**
 *
 * @author Alvaro Lozano
 */
public class Main {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static void main(String[] args) {

        // Crear nueva llamada
        CrearNuevaLlamada();

        // Consultar registro con find() y getReference()
        ConsultarRegistroExistente();
        ConsultarRegistroConGetReference();

        // Modificar registro existente
        ModificarRegistroExistente();

        // Eliminar registro recién creado
        EliminarRegistroExistente(1000072);

        // Gestión de estados: detach, clear, merge
        GestionEstados();
    }

    /// /////////////////////////////////////////////////////////
    // CREAR NUEVA LLAMADA
    public static Integer CrearNuevaLlamada() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        Integer codigoGenerado = null;
        Integer simLlamante = 617478396;

        try {
            tx = em.getTransaction();
            tx.begin();

            LlamadasEmitida nuevaLlamada = new LlamadasEmitida();

            // Obtener tarjeta telefónica existente
            TarjetasTelefonica tarjeta = em.find(TarjetasTelefonica.class, simLlamante);

            if (tarjeta == null) {
                System.out.println("La tarjeta 617478396 no existe. Creando una de ejemplo...");
                tarjeta = new TarjetasTelefonica();
                tarjeta.setId(simLlamante);

                Agente agente = em.find(Agente.class, 9);
                if (agente == null) {
                    System.out.println("El agente con ID 9 no existe. Creando uno de ejemplo...");
                    agente = new Agente();
                    agente.setId(9);
                    agente.setNombreAgente("Agente Demo");
                    agente.setFraseClave("Clave123");
                    em.persist(agente); // persist() estándar JPA, devuelve void
                }

                tarjeta.setCodigoAgenteAsociado(agente);
                em.persist(tarjeta);
            }

            nuevaLlamada.setSimLlamante(tarjeta);
            nuevaLlamada.setNumeroLlamado(987654321);
            nuevaLlamada.setDuracionLlamada(300);
            nuevaLlamada.setImporteLlamada(12.50f);
            nuevaLlamada.setId(1000072);

            // persist() hace que el objeto pase de transitorio a persistente
            em.persist(nuevaLlamada);

            // commit de la transacción
            tx.commit();

            codigoGenerado = nuevaLlamada.getId();
            System.out.println("Nueva llamada creada con CODIGO_LLAMADA = " + codigoGenerado);

            /* Nota:
             * persist() es JPA estándar y devuelve void.
             * save() es específico de Hibernate y devuelve la entidad guardada.
             * Recomendación: usar persist() cuando trabajamos con JPA puro.
             */

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("Error: " + e.getMessage());
        } finally {
            em.close();
        }

        return codigoGenerado;
    }

    /// /////////////////////////////////////////////////////////
    // CONSULTAR CON find()
    private static void ConsultarRegistroExistente() {
        EntityManager em = emf.createEntityManager();

        try {
            int idBuscar = 1000072;
            System.out.println("1. Usando find():");
            LlamadasEmitida llamada1 = em.find(LlamadasEmitida.class, idBuscar);

            if (llamada1 != null) {
                System.out.println("Número llamado: " + llamada1.getNumeroLlamado() +
                        "\nId: " + llamada1.getId() +
                        "\nDuración: " + llamada1.getDuracionLlamada() +
                        "\nImporte: " + llamada1.getImporteLlamada() +
                        "\nSim del llamante: " + llamada1.getSimLlamante());
            } else {
                System.out.println("Llamada no encontrada");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /// /////////////////////////////////////////////////////////
    // CONSULTAR CON getReference()
    private static void ConsultarRegistroConGetReference() {
        EntityManager em = emf.createEntityManager();

        try {
            int idBuscar = 1000072;
            System.out.println("2. Usando getReference():");

            // getReference devuelve un proxy; la consulta se ejecuta al acceder a los campos
            LlamadasEmitida llamadaRef = em.getReference(LlamadasEmitida.class, idBuscar);

            // Acceso a campos dispara SELECT
            System.out.println("Número llamado: " + llamadaRef.getNumeroLlamado());
            System.out.println("Duración: " + llamadaRef.getDuracionLlamada());
            System.out.println("Importe: " + llamadaRef.getImporteLlamada());

        } catch (EntityNotFoundException enf) {
            System.out.println("Registro con ID " + 1000072 + " no existe (EntityNotFoundException).");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            em.close();
        }

        /* Nota:
         * find() ejecuta la consulta inmediatamente y devuelve null si no existe.
         * getReference() devuelve un proxy, y lanza EntityNotFoundException si no existe al acceder a campos.
         */
    }

    /// /////////////////////////////////////////////////////////
    // MODIFICAR REGISTRO
    public static void ModificarRegistroExistente() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            int idBuscar = 1000054;
            LlamadasEmitida llamada1 = em.find(LlamadasEmitida.class, idBuscar);

            if (llamada1 != null) {
                float importeAnterior = llamada1.getImporteLlamada();
                float nuevoImporte = importeAnterior * 1.10f;
                llamada1.setImporteLlamada(nuevoImporte);
                System.out.println("Importe actualizado de la llamada: " + nuevoImporte);
            } else {
                System.out.println("Llamada no encontrada");
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("Rollback ejecutado: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /// /////////////////////////////////////////////////////////
    // ELIMINAR REGISTRO
    public static void EliminarRegistroExistente(int codigoLlamada) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);
            if (llamada != null) {
                em.remove(llamada);
                tx.commit();
                System.out.println("Registro eliminado correctamente");
            } else {
                System.out.println("Llamada no encontrada o el objeto no existe");
            }

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /// /////////////////////////////////////////////////////////
    // GESTIÓN DE ESTADOS
    public static void GestionEstados() {
        System.out.println("--- PASO 6: GESTIÓN DE ESTADOS ---");

        System.out.println("A) Disociar con detach():");
        apartado_Detach(1000054);

        System.out.println("B) Disociar con clear():");
        apartado_Clear(1000071);

        System.out.println("C) Reasociar con merge():");
        apartado_Merge();
    }

    private static void apartado_Detach(int codigoLlamada) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);
            if (llamada != null) {
                em.detach(llamada);
                llamada.setImporteLlamada(llamada.getImporteLlamada() + 5);
                System.out.println("Objeto en DETACHED modificado en memoria (NO guardado en BD)");
            }
            tx.commit();
        } finally {
            em.close();
        }
    }

    private static void apartado_Clear(int codigoLlamada) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);
            if (llamada != null) {
                em.clear();
                llamada.setImporteLlamada(llamada.getImporteLlamada() + 10);
                System.out.println("Objeto modificado en memoria tras clear() (NO guardado en BD)");
            }
            tx.commit();
        } finally {
            em.close();
        }
    }

    private static void apartado_Merge() {
        System.out.println("=== REASOCIAR CON MERGE ===");

        EntityManager em1 = emf.createEntityManager();
        LlamadasEmitida llamadaDetached = em1.find(LlamadasEmitida.class, 1000054);
        em1.close();

        if (llamadaDetached != null) {
            llamadaDetached.setImporteLlamada(llamadaDetached.getImporteLlamada() + 20);
            System.out.println("Objeto modificado en DETACHED.");
        }

        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx = em2.getTransaction();

        try {
            tx.begin();
            LlamadasEmitida entidadPersistente = em2.merge(llamadaDetached);
            System.out.println("Entidad reasociada con merge(), estado PERSISTENTE.");
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.out.println("Error en merge(): " + e.getMessage());
        } finally {
            em2.close();
        }
    }
}
