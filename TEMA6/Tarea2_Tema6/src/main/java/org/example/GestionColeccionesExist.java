package org.example;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import java.lang.reflect.InvocationTargetException;

public class GestionColeccionesExist {

    // Constantes de conexión indicadas en el cheatsheet
    // DRIVER: clase del driver XML:DB de eXistDB
    // URI: apunto directamente a la colección /db/Formacion, que será la colección padre
    // USER y PASS: credenciales de acceso a la base de datos
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db";
    private static final String USER = "admin";
    private static final String PASS = "";

    public static void main(String[] args) {

        // Variable donde guardaré la colección padre (/db/Formacion)
        // La inicializo a null para poder cerrarla correctamente en el finally
        Collection formacionCol = null;

        try {
            // PASO 1 (cheatsheet): Cargar la clase del driver de eXistDB
            // Con Class.forName me aseguro de que el driver está disponible en tiempo de ejecución
            Class<?> cl = Class.forName(DRIVER);

            // PASO 2: Instanciar el driver usando reflexión
            Database database = (Database) cl.getDeclaredConstructor().newInstance();

            // PASO 3: Registrar el driver en DatabaseManager
            // A partir de este momento ya puedo pedir colecciones a la base de datos
            DatabaseManager.registerDatabase(database);

            // PASO 4: Obtener la colección padre /db
            formacionCol = DatabaseManager.getCollection(URI, USER, PASS);

            // Compruebo que la colección exista realmente
            // Si es null, significa que la URI es incorrecta o la colección no existe
            if (formacionCol == null) {
                System.out.println("No se pudo acceder a /db/Formacion. Comprueba que la colección existe, la URI y las credenciales.");
                return;
            }

            System.out.println("Conectado correctamente a la colección: " + formacionCol.getName());

            // PASO 5: Obtener el servicio de gestión de colecciones
            // Este servicio es el que permite crear y eliminar subcolecciones
            CollectionManagementService cms =
                    (CollectionManagementService) formacionCol.getService(
                            "CollectionManagementService", "1.0");

            if (cms == null) {
                System.out.println("No se pudo obtener CollectionManagementService. Revisa permisos o configuración.");
                return;
            }

            // PASO 6: Crear las subcolecciones solicitadas en el enunciado
            // /db/Formacion/Alumnos
            // /db/Formacion/Error
            crearColeccionSiNoExiste(formacionCol, cms, "Alumnos");
            crearColeccionSiNoExiste(formacionCol, cms, "Error");



            // PASO 6 (segunda parte): Eliminar la colección Error recién creada
            // Esto sirve para comprobar también el borrado de colecciones
            eliminarColeccionSiExiste(formacionCol, cms, "Error");

        } catch (ClassNotFoundException e) {
            // Error típico cuando falta la dependencia exist-core en el pom.xml
            System.out.println("ERROR: No se encontró el driver de eXistDB (" + DRIVER + "). Revisa el pom.xml.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            // Errores relacionados con la instanciación del driver mediante reflexión
            System.out.println("ERROR: No se pudo instanciar el driver de eXistDB.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (XMLDBException e) {
            // Errores propios de la API XML:DB (conexión, permisos, colecciones, etc.)
            System.out.println("ERROR XMLDB: Problema conectando o gestionando colecciones en eXistDB.");
            System.out.println("Código: " + e.errorCode);
            System.out.println("Detalle: " + e.getMessage());

        } finally {
            // PASO 7: Cerrar la conexión
            // Este bloque se ejecuta siempre, haya error o no,
            // asegurando que se liberan los recursos correctamente
            if (formacionCol != null) {
                try {
                    formacionCol.close();
                    System.out.println("Conexión cerrada correctamente.");
                } catch (XMLDBException e) {
                    System.out.println("Aviso: error al cerrar la colección.");
                    System.out.println("Detalle: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Crea una subcolección dentro de /db/Formacion si no existe ya.
     */
    private static void crearColeccionSiNoExiste(
            Collection padre,
            CollectionManagementService cms,
            String nombre) throws XMLDBException {

        // Antes de crear la colección, compruebo si ya existe
        // para evitar errores o duplicados
        if (existeSubcoleccion(padre, nombre)) {
            System.out.println("La colección /db/Formacion/" + nombre + " ya existe (no la vuelvo a crear).");
            return;
        }

        // Si no existe, la creo usando CollectionManagementService
        cms.createCollection(nombre);
        System.out.println("Colección creada correctamente: /db/Formacion/" + nombre);
    }

    /**
     * Elimina una subcolección dentro de /db/Formacion si existe.
     */
    private static void eliminarColeccionSiExiste(
            Collection padre,
            CollectionManagementService cms,
            String nombre) throws XMLDBException {

        // Compruebo primero que la colección exista
        if (!existeSubcoleccion(padre, nombre)) {
            System.out.println("La colección /db/Formacion/" + nombre + " no existe (no se puede borrar).");
            return;
        }

        // Elimino la colección (y todo su contenido)
        cms.removeCollection(nombre);
        System.out.println("Colección eliminada correctamente: /db/Formacion/" + nombre);
    }

    /**
     * Comprueba si existe una subcolección con ese nombre dentro de la colección padre.
     */
    private static boolean existeSubcoleccion(Collection padre, String nombre)
            throws XMLDBException {

        // Obtengo la lista de subcolecciones hijas
        String[] hijas = padre.listChildCollections();
        if (hijas == null) return false;

        // Recorro el array buscando el nombre indicado
        for (String h : hijas) {
            if (h.equals(nombre)) {
                return true;
            }
        }
        return false;
    }
}
