package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


public class Main {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public static void main(String[] args) {

        //CrearNuevaLlamada();

        //ConsultarRegistroExistente();

        //ModificarRegistroExistente();

        //EliminarRegistroExistente(1000072);

        GestionEstados();
    }

    /// ///////////////////////////////////

    //Crear metodo para crear nueva llamada
    public static Integer CrearNuevaLlamada() {
        // EntityManager: Gestor que maneja las operaciones con la BD (UNO por operación)
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = null;

        Integer codigoGenerado = null;

        Integer simLlamante = 617478396;
        //Bloque try-catch-finally
        try {
            //1º Iniciamos una transacción
            tx = em.getTransaction();
            tx.begin();
            //2º Creamos el objeto en estado transitorio (aquí es posible que necesiteis objetos de otro tipo de entidades)
            LlamadasEmitida nuevaLlamada = new LlamadasEmitida();
            //3º configuramos la nueva llama
            //Necesitamos primero obtener la tarjeta telefonica (FK)
            //Suponemos que existe una tarjeta con NUMERO_SIM = 617478396
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
                    em.persist(agente);
                }
                tarjeta.setCodigoAgenteAsociado(agente);
                em.persist(tarjeta);
            }

            nuevaLlamada.setSimLlamante(tarjeta);
            nuevaLlamada.setNumeroLlamado(987654321);
            nuevaLlamada.setDuracionLlamada(300);
            nuevaLlamada.setImporteLlamada(12.50f);
            nuevaLlamada.setId(1000072);

            //4º usamos persist() para ese objeto
            em.persist(nuevaLlamada);

            //5º hacemos commit
            tx.commit();

            codigoGenerado = nuevaLlamada.getId();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
                System.err.println("Error: transacción revertida");
            }
            System.err.println("Error: " + e.getMessage());

        } finally {
            em.close();
            System.out.println("EntityManager cerrado\n");
        }
        return codigoGenerado;
    }

    /// //////////////////////////////////////////////////////////

    private static void ConsultarRegistroExistente() {
        EntityManager em = emf.createEntityManager();

        try {
            int idBuscar = 1000072;

            System.out.println("1. Usando find():");
            LlamadasEmitida llamada1 = em.find(LlamadasEmitida.class, idBuscar);

            if (llamada1 != null) {
                System.out.println("El numero llamado es: " + llamada1.getNumeroLlamado() +
                        " \nEl Id es: "+ llamada1.getId() +
                        " \nLa duración es: "+ llamada1.getDuracionLlamada() +
                        " \nEl Importe es: "+  llamada1.getImporteLlamada() +
                        " \nEl sim del llamante es: "+ llamada1.getSimLlamante());
            } else {
                System.out.println("Llamada no encontrada");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /// ////////////////////////////////



    public static void ModificarRegistroExistente() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            System.out.println("Transacción iniciada");

            //1º Encontrar registro en la tabla y traerlo a Java
            int idBuscar = 1000054;
            System.out.println("1. Usando find()...");
            LlamadasEmitida llamada1 = em.find(LlamadasEmitida.class, idBuscar);

            //2º Modificar el objeto
            if (llamada1 != null) {
                float importeAnterior = llamada1.getImporteLlamada();
                System.out.println("El importe anterior es: " + importeAnterior);

                //Modificamos el objeto en memoria
                float nuevoImporte = importeAnterior *1.10f; //Incremento del 10%
                llamada1.setImporteLlamada(nuevoImporte ); //tambíen valdría llamada1.setImporteLlamada(nuevoImporte + 0.1f);
                System.out.println("El nuevo importe de la llamda es: " + nuevoImporte); //Para mosrar el nuevo importe
            } else {
                System.out.println("Llamada no encontrada");
            }

            //3º Hacer el commit
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
                System.err.println("Rollback ejecutado");
            }
            System.err.println("Causa: " + e.getMessage());
        } finally {
            em.close();
        }

    }

    /// ///////////////////////////////////////

    public static void EliminarRegistroExistente(int codigoLlamada) {
        System.out.println("Eliminando llamada con ID = "+ codigoLlamada + "\n");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {

            tx = em.getTransaction();
            tx.begin();
            System.out.println("Transcción iniciada");

            //Recuperamos (buscamos) el objeto que queremos eliminar de la tabla
            System.out.println("Recuperamos el objeto con find()...");
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);
                    if(llamada != null) {

                        //Eliminar con remove()
                        System.out.println("Objeto recuperado en estado PERSISTENTE, eliminando...");
                        em.remove(llamada);

                        //Realizar el conmit
                        System.out.println("\n Ejecutando commit()...");
                        tx.commit();

                    }else{
                        System.out.println("Llamada no encontrada o el Objeto no existe");
                    }

        }catch (Exception e) {
            System.out.println("Error al eliminar un registro");
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Error: " + e.getMessage());
        }finally {
            em.close();
        }
    }

    /// //////////////////////////////////////////

    public static void GestionEstados(){
        System.out.println("--- PASO 6: GESTIÓN DE ESTADOS ---");

        // A) DETACH - Disociar una entidad especifíca
        System.out.println("A) Disociar con detach():");
        apartado_Detach(1000054);

        // B) CLEAR - Disociar TODAS  las entidades
        System.out.println("B) Disociar con eliminar():");
        apartado_Clear(1000071);

        // C) MERGE - Reasociar una entidad disociada
        apartado_Merge();

    }

    private static void apartado_Detach(int codigoLlamada){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            //INICIAMOS TRANSACCIÓN
            tx.begin();
            System.out.println("Transacción iniciada (DETACH)");

            //1. Obtenemos la llamada --> estado PERSISTENTE
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);

            if(llamada != null){

                System.out.println("Objeto encontrado. Estado PERSISTENTE");

                //2. Lo pasamos a estado DETACHED
                em.detach(llamada);
                System.out.println("Objeto cambiado a estado DETACHED");

                //3. Modificar el importe estando DETACHED
                float importeAnterior = llamada.getImporteLlamada();
                llamada.setImporteLlamada(importeAnterior + 5);
                System.out.println("Importe cambiado en memoria local, no se guardara en la base de datos");

            }else{
                System.out.println("No existe el registro con ID" + codigoLlamada);
            }

            tx.commit(); // NO guarda no guarda los cambios del objeto DETACHED





        }catch(Exception e){


        }finally{
            em.close();
        }
    }

    private static void apartado_Clear(int codigoLlamada){

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            System.out.println("Transacción iniciada (CLEAR)");

            //1. Obtenemos una llamada culaquiera
            LlamadasEmitida llamada = em.find(LlamadasEmitida.class, codigoLlamada);

            if(llamada != null){
                System.out.println("Objeto encontrado. Estado PERSISTENTE");

                //2. Desasociamos TODAS las entidades
                em.clear();
                System.out.println("EntityManager limpiado. Todas las entidades pasan a DETACHED.");


                //3. Intentamos modificarla
                float importeAnterior = llamada.getImporteLlamada();
                llamada.setImporteLlamada(importeAnterior + 10);
                System.out.println("Importe cambiado en memoria (NO SE GUARDA EN LA BD");
            }else{
                System.out.println("No existe el registro con ID: " + codigoLlamada);
            }
            tx.commit();
        }catch(Exception e){


        }finally{
            em.close();
        }


    }

    private static void apartado_Merge(){

        System.out.println("=== REASOCIAR CON MERGE ===");

        // Recuperamos un objeto DETACHED desde la BD
        EntityManager em1 = emf.createEntityManager();
        LlamadasEmitida llamadaDetached = em1.find(LlamadasEmitida.class, 1000054);
        em1.close(); // <-- al cerrar, pasa a DETACHED

        // Lo modificamos mientras está DETACHED
        if (llamadaDetached != null) {
            float importeAnterior = llamadaDetached.getImporteLlamada();
            llamadaDetached.setImporteLlamada(importeAnterior + 20);
            System.out.println("Objeto modificado en estado DETACHED.");
        }

        // ---- Realizamos el MERGE ----
        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx = em2.getTransaction();

        try {
            tx.begin();

            // merge devuelve una COPIA PERSISTENTE DEL OBJETO
            LlamadasEmitida entidadPersistente = em2.merge(llamadaDetached);

            System.out.println("Entidad reasociada con merge(). Estado PERSISTENTE nuevamente.");

            tx.commit();
            System.out.println("Cambios guardados correctamente.");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.out.println("Error en merge(): " + e.getMessage());
        } finally {
            em2.close();
        }
    }


}


