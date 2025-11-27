package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityNotFoundException;

/**
 * =======================================================
 *  EJEMPLO DIDÁCTICO DE MAPEADO OBJETO <--> RELACIONAL (JPA)
 *  + OPERACIONES CRUD Y CICLO DE VIDA DE ENTIDADES
 * =======================================================
 *  Este código muestra:
 *    - Cómo una TABLA SQL se mapea a una CLASE Java (@Entity)
 *    - Cómo una FILA/REGISTRO se representa como un OBJETO
 *    - Cómo las CLAVES PRIMARIAS (PK) se mapean a atributos @Id
 *    - Cómo PERSIST() → INSERT SQL, REMOVE() → DELETE SQL
 *    - Cómo FIND() y GETREFERENCE() generan SELECT SQL
 *    - Estados de objetos JPA: TRANSIENT → MANAGED → DETACHED → MERGED
 *
 *  Ideal para reutilizar en exámenes de Acceso a Datos / ORM
 * =======================================================
 */
public class Main {

    // EntityManagerFactory es pesado de crear: almacena la configuración del ORM (persistence.xml)
    // Se debe crear UNA SOLA VEZ y reutilizarse en toda la aplicación
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static void main(String[] args) {

        // 1) INSERT SQL → Creamos un objeto TRANSIENT y lo hacemos MANAGED con persist()
        CrearNuevaLlamada();

        // 2) SELECT SQL inmediato → Recuperamos entidad por PK, si no existe devuelve null
        ConsultarRegistroExistente();

        // 3) SELECT SQL diferido → Devuelve PROXY (proxy = objeto falso hasta acceder a sus datos)
        //     Si no existe, lanza EntityNotFoundException al usar getters
        ConsultarRegistroConGetReference();

        // 4) UPDATE SQL automático en commit → Modificamos entidad MANAGED en memoria y confirmamos
        ModificarRegistroExistente();

        // 5) DELETE SQL → Buscamos entidad por PK y la eliminamos con remove()
        EliminarRegistroExistente(1000072);

        // 6) Ejercicio NO CRUD → Simulamos paso entre estados de entidad (detach, clear, merge)
        GestionEstados();
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  INSERT SQL → Crear y guardar un nuevo OBJETO en la BD
    //  Mapeo de clase → tabla, mapeo de atributos → columnas
    // ----------------------------------------------------------
    public static Integer CrearNuevaLlamada() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        Integer codigoGenerado = null;
        Integer simLlamante = 617478396;

        try {
            tx = em.getTransaction();
            tx.begin(); // INICIO de transacción SQL (BEGIN)

            LlamadasEmitida nuevaLlamada = new LlamadasEmitida();
            // Aquí el objeto es TRANSIENT (no existe en BD ni está gestionado)

            // FK mapeada como objeto → buscamos la tarjeta del llamante por su PK
            TarjetasTelefonica tarjeta = em.find(TarjetasTelefonica.class, simLlamante);
            // find() = SELECT SQL inmediato por PK

            if (tarjeta == null) {
                System.out.println("La tarjeta 617478396 no existe. Creando una de ejemplo...");
                tarjeta = new TarjetasTelefonica();
                tarjeta.setId(simLlamante);
                // Ahora sigue siendo TRANSIENT

                // Relación con otra entidad (tabla relacionada Agente)
                Agente agente = em.find(Agente.class, 9);
                if (agente == null) {
                    System.out.println("El agente con ID 9 no existe. Creando uno de ejemplo...");
                    agente = new Agente();
                    agente.setId(9);
                    agente.setNombreAgente("Agente Demo");
                    agente.setFraseClave("Clave123");
                    // Aún TRANSIENT

                    em.persist(agente); // convierte agente a MANAGED → INSERT SQL en commit()
                }

                // La FK de la tarjeta se representa como un objeto relacionado en Java
                tarjeta.setCodigoAgenteAsociado(agente);
                em.persist(tarjeta); // convierte tarjeta a MANAGED → INSERT SQL en commit()
            }

            // Mapeo de relación: la FK de la llamada se asigna con un objeto tarjeta MANAGED
            nuevaLlamada.setSimLlamante(tarjeta);

            // Rellenamos atributos (propiedades del objeto = columnas de la tabla SQL)
            nuevaLlamada.setNumeroLlamado(987654321);
            nuevaLlamada.setDuracionLlamada(300);
            nuevaLlamada.setImporteLlamada(12.50f);
            nuevaLlamada.setId(1000072);
            // setId() establece la CLAVE PRIMARIA, mapeada al campo @Id → será PK en SQL

            em.persist(nuevaLlamada);
            // Ahora pasa de TRANSIENT → MANAGED (persistente), en commit() será INSERT SQL

            tx.commit();
            // TRANSLATE: todos los objetos MANAGED se sincronizan con BD (INSERTS+UPDATES SQL)
            // FIN de transacción SQL (COMMIT)

            codigoGenerado = nuevaLlamada.getId();
            System.out.println("Nueva llamada creada con CODIGO_LLAMADA = " + codigoGenerado);

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            // Fallo → SQL ROLLBACK, se descartan INSERT/UPDATE/DELETE SQL
            System.err.println("Error: " + e.getMessage());
        } finally {
            em.close();
            // Cerrar EntityManager = Desconectar sesión ORM (objetos pasan a DETACHED si eran MANAGED)
        }

        return codigoGenerado;
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  SELECT SQL → Consultar una entidad por PK con find()
    //  Consulta inmediata. Si no existe, devuelve null.
    // ----------------------------------------------------------
    private static void ConsultarRegistroExistente() {
        EntityManager em = emf.createEntityManager();

        try {
            int idBuscar = 1000072;

            System.out.println("1. Usando find():");
            // Buscamos el registro en BD por PK → se mapea a un objeto MANAGED si existe
            LlamadasEmitida llamada1 = em.find(LlamadasEmitida.class, idBuscar);

            if (llamada1 != null) {
                System.out.println("Número llamado: " + llamada1.getNumeroLlamado() +
                        "\nId (PK): " + llamada1.getId() +
                        "\nDuración: " + llamada1.getDuracionLlamada() +
                        "\nImporte: " + llamada1.getImporteLlamada() +
                        "\nTarjeta del llamante (objeto FK): " + llamada1.getSimLlamante());
                // Cada getter accede a propiedades del objeto = valores mapeados desde columnas SQL
            } else {
                System.out.println("Llamada no encontrada (find() devuelve null si no existe)");
            }

        } catch (Exception e) {
            System.out.println("Error en SELECT find(): " + e.getMessage());
        } finally {
            em.close(); // Finalizamos sesión ORM
        }
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  SELECT SQL → Consultar usando getReference(), con proxy
    //  La consulta SQL no se ejecuta hasta usar getters.
    //  Si no existe el registro → EntityNotFoundException al usar getters.
    // ----------------------------------------------------------
    private static void ConsultarRegistroConGetReference() {
        EntityManager em = emf.createEntityManager();

        try {
            int idBuscar = 1000072;

            System.out.println("2. Usando getReference():");

            // Esto devuelve un PROXY JPA (objeto falso temporal sin datos cargados)
            LlamadasEmitida llamadaRef = em.getReference(LlamadasEmitida.class, idBuscar);

            // Al usar getters, Hibernate/JPA ejecuta SELECT SQL y rellena el objeto real
            System.out.println("Número llamado: " + llamadaRef.getNumeroLlamado());
            System.out.println("Duración: " + llamadaRef.getDuracionLlamada());
            System.out.println("Importe: " + llamadaRef.getImporteLlamada());

        } catch (EntityNotFoundException enf) {
            System.out.println("Registro NO existe: getReference() lanza EntityNotFoundException al acceder a datos.");
        } catch (Exception e) {
            System.out.println("Error genérico en getReference(): " + e.getMessage());
        } finally {
            em.close(); // Cerramos sesión ORM
        }

        /* Nota rápida para examen:
         *  - find() = SELECT inmediato, null si no existe.
         *  - getReference() = proxy, SELECT diferido, excepción si no existe al usar getters.
         */
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  UPDATE SQL automático → Modificar objeto MANAGED en memoria
    //  No hace falta persist() extra si está MANAGED, el UPDATE SQL
    //  se genera automáticamente en commit(), pero aquí se deja así
    //  porque es un ejercicio académico.
    // ----------------------------------------------------------
    public static void ModificarRegistroExistente() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin(); // BEGIN SQL

            int idBuscar = 1000054;

            // Buscamos la entidad por PK → pasa a MANAGED si existe
            LlamadasEmitida llamada1 = em.find(LlamadasEmitida.class, idBuscar);

            if (llamada1 != null) {
                float importeAnterior = llamada1.getImporteLlamada();
                float nuevoImporte = importeAnterior * 1.10f; // Incremento 10%
                llamada1.setImporteLlamada(nuevoImporte);
                // set...() modifica propiedades del objeto = columnas SQL en la tabla

                System.out.println("Importe actualizado en memoria. UPDATE SQL al hacer commit()");
            } else {
                System.out.println("Llamada no encontrada, no se realiza UPDATE SQL");
            }

            tx.commit();
            // JPA/Hibernate sincroniza cambios de objetos MANAGED a UPDATE SQL

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("UPDATE fallido → Rollback SQL: " + e.getMessage());
        } finally {
            em.close(); // Los objetos MANAGED pasan a DETACHED al cerrar sesión
        }
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  DELETE SQL → remove()
    //  Busca por PK → si existe, se marca para borrar,
    //  y en commit() JPA genera DELETE SQL
    // ----------------------------------------------------------
    public static void EliminarRegistroExistente(int codigoLlamada) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin(); // BEGIN SQL

            // SELECT SQL por PK → si existe, queda MANAGED
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);

            if (llamada != null) {
                em.remove(llamada);
                // Ahora JPA marca la entidad MANAGED para borrar → DELETE SQL en commit()

                tx.commit(); // COMMIT SQL + DELETE SQL generado automáticamente
                System.out.println("Registro eliminado correctamente (DELETE SQL ejecutado)");
            } else {
                System.out.println("No existe el PK solicitado → no se genera DELETE SQL");
            }

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Error DELETE: " + e.getMessage());
        } finally {
            em.close(); // Cerramos sesión ORM
        }
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    // GESTIÓN DE ESTADOS DE ENTIDADES (Ciclo de vida JPA)
    // No genera SQL, solo muestra comportamiento en memoria
    // Detalla:
    //   A) detach() → desasocia 1 objeto MANAGED → pasa a DETACHED
    //   B) clear()  → desasocia todos los objetos → todos a DETACHED
    //   C) merge()  → rehace MANAGED → UPDATE SQL al commit si hubo cambios
    // ----------------------------------------------------------
    public static void GestionEstados() {
        System.out.println("--- PASO 6: GESTIÓN DE ESTADOS (CICLO DE VIDA JPA) ---");
        System.out.println("Estos pasos no consultan listas ni HQL: analizan estados en memoria.");

        System.out.println("\nA) detach() → 1 objeto MANAGED pasa a DETACHED (cambios solo en memoria):");
        apartado_Detach(1000054);

        System.out.println("\nB) clear() → Todos los objetos gestionados pasan a DETACHED (cambios solo memoria):");
        apartado_Clear(1000071);

        System.out.println("\nC) merge() → Reasocia objeto DETACHED y pasa a MANAGED (UPDATE SQL en commit si cambió):");
        apartado_Merge();
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  detach() → Estado DETACHED
    //  Modifica el objeto fuera de sesión → NO genera UPDATE SQL
    // ----------------------------------------------------------
    private static void apartado_Detach(int codigoLlamada) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin(); // BEGIN SQL (aunque no se sincronizarán cambios tras el detach())

            // SELECT SQL por PK → el objeto existe y es MANAGED en este punto
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);

            if (llamada != null) {
                em.detach(llamada);
                // Hibernate/JPA deja de gestionar el objeto → ahora es DETACHED

                llamada.setImporteLlamada(llamada.getImporteLlamada() + 5);
                // Este cambio solo ocurre en memoria → NO generará UPDATE SQL

                System.out.println("Objeto DETACHED modificado solo en memoria (NO guardado en BD)");
            }

            tx.commit();
            // No habrá UPDATE SQL porque el objeto no está MANAGED

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close(); // Sesión cerrada
        }

        /* Resumen para examen:
         *  - Antes de detach() → MANAGED.
         *  - Después de detach() → DETACHED.
         *  - commit() NO sincroniza en UPDATE SQL si está DETACHED.
         */
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  clear() → vacía la sesión ORM completa
    //  El objeto recuperado se vuelve DETACHED → cambios solo memoria
    // ----------------------------------------------------------
    private static void apartado_Clear(int codigoLlamada) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin(); // BEGIN SQL

            // SELECT inmediato por PK → el objeto es MANAGED inicialmente
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);

            if (llamada != null) {
                em.clear();
                // Hibernate/JPA vacía la sesión → TODOS los MANAGED pasan a DETACHED
                // incluida esta llamada

                llamada.setImporteLlamada(llamada.getImporteLlamada() + 10);
                System.out.println("Objeto DETACHED tras clear() → cambio solo en memoria (NO BD)");
            }

            tx.commit(); // COMMIT SQL sin UPDATE porque no hay entidades MANAGED

        } finally {
            em.close(); // Cerramos sesión ORM
        }

        /* Resumen para examen:
         *  - clear() afecta a TODAS las entidades en sesión.
         *  - No produce UPDATE SQL hasta merge().
         */
    }

    /// /////////////////////////////////////////////////////////
    // ----------------------------------------------------------
    //  merge() → Reasociar un objeto DETACHED
    //  Devuelve una nueva instancia MANAGED → commit() creará UPDATE SQL si hubo cambios.
    // ----------------------------------------------------------
    private static void apartado_Merge() {
        System.out.println("=== APARTADO MERGE: REASOCIAR OBJETO DETACHED ===");

        // 1) Obtenemos la entidad y cerramos sesión → la entidad pasa a DETACHED
        EntityManager em1 = emf.createEntityManager();
        LlamadasEmitida llamadaDetached = em1.find(LlamadasEmitida.class, 1000054);
        em1.close(); // Ahora el objeto es DETACHED

        if (llamadaDetached != null) {
            llamadaDetached.setImporteLlamada(llamadaDetached.getImporteLlamada() + 20);
            System.out.println("Objeto DETACHED modificado en memoria (+20 importe). Aún no impacta BD.");
        }

        // 2) nueva sesión → merge() devuelve instancia MANAGED
        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx = em2.getTransaction();

        try {
            tx.begin(); // BEGIN SQL

            // merge() = el objeto vuelve a ser gestionado (MANAGED) y listo para sincronizar
            LlamadasEmitida entidadPersistente = em2.merge(llamadaDetached);

            System.out.println("merge() aplicado → Nuevo objeto MANAGED devuelto por Hibernate/JPA");

            tx.commit();
            // Sincroniza cambios → genera UPDATE SQL si el objeto MANAGED tiene diferencias

            System.out.println("commit() realizado → UPDATE SQL ejecutado en BD si hubo cambios.");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.out.println("Error en merge(): " + e.getMessage());
        } finally {
            em2.close(); // Cerramos sesión ORM
        }

        /* Resumen para examen:
         *  - merge() no actualiza el objeto original, devuelve nuevo MANAGED.
         *  - UPDATE SQL solo ocurre al commit() si la entidad está en MANAGED.
         */
    }
}

