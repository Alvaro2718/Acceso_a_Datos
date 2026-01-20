package org.example;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
public class GestionColeccionesExist {

    // Constantes de conexión (como en el documento del profesor)
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/Formacion";
    private static final String USER = "admin";
    private static final String PASS = ""; // si la cambiaste, pon aquí tu contraseña

    public static void main( String[] args ) {


        Collection formacionCol = null;

        try {
            // 1) Cargar el driver de eXistDB (XML:DB)
            Class<?> cl = Class.forName(DRIVER);

            // 2) Instanciar y registrar el driver en DatabaseManager
            Database database = (Database) cl.getDeclaredConstructor().newInstance();
            DatabaseManager.registerDatabase(database);

            // 3) Conectar a la colección padre /db/Formacion
            formacionCol = DatabaseManager.getCollection(URI, USER, PASS);

            if (formacionCol == null) {
                System.out.println("No se pudo acceder a /db/Formacion. Comprueba que la colección existe, la URI y las credenciales.");
                return;
            }

            System.out.println("Conectado correctamente a: " + formacionCol.getName());

            // 4) Obtener el servicio de gestión de colecciones
            CollectionManagementService cms = (CollectionManagementService)
                    formacionCol.getService("CollectionManagementService", "1.0");

            if (cms == null) {
                System.out.println("No se pudo obtener CollectionManagementService. Revisa permisos o configuración.");
                return;
            }

            // 5) Crear subcolecciones /db/Formacion/Alumnos y /db/Formacion/Error
            crearColeccionSiNoExiste(formacionCol, cms, "Alumnos");
            crearColeccionSiNoExiste(formacionCol, cms, "Error");

            // 6) Eliminar la colección /db/Formacion/Error
            eliminarColeccionSiExiste(formacionCol, cms, "Error");

        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No se encontró el driver de eXistDB (" + DRIVER + "). Revisa el pom.xml.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("ERROR: No se pudo instanciar el driver de eXistDB.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (XMLDBException e) {
            System.out.println("ERROR XMLDB: Problema conectando o gestionando colecciones en eXistDB.");
            System.out.println("Código: " + e.errorCode);
            System.out.println("Detalle: " + e.getMessage());

        } finally {
            // 7) Cerrar conexión (liberar recursos)
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
    private static void crearColeccionSiNoExiste(Collection padre, CollectionManagementService cms, String nombre)
            throws XMLDBException {

        // Si ya existe, padre.getChildCollection(nombre) devuelve una referencia (no siempre null según implementación),
        // así que lo más fiable es comprobar si aparece en listChildCollections().
        if (existeSubcoleccion(padre, nombre)) {
            System.out.println("La colección /db/Formacion/" + nombre + " ya existe (no la vuelvo a crear).");
            return;
        }

        cms.createCollection(nombre);
        System.out.println("Colección creada correctamente: /db/Formacion/" + nombre);
    }

    /**
     * Elimina una subcolección dentro de /db/Formacion si existe.
     */
    private static void eliminarColeccionSiExiste(Collection padre, CollectionManagementService cms, String nombre)
            throws XMLDBException {

        if (!existeSubcoleccion(padre, nombre)) {
            System.out.println("La colección /db/Formacion/" + nombre + " no existe (no se puede borrar).");
            return;
        }

        cms.removeCollection(nombre);
        System.out.println("Colección eliminada correctamente: /db/Formacion/" + nombre);
    }

    /**
     * Comprueba si existe una subcolección con ese nombre dentro de la colección padre.
     */
    private static boolean existeSubcoleccion(Collection padre, String nombre) throws XMLDBException {
        String[] hijas = padre.listChildCollections();
        if (hijas == null) return false;

        for (String h : hijas) {
            if (h.equals(nombre)) {
                return true;
            }
        }
        return false;
    }
}
