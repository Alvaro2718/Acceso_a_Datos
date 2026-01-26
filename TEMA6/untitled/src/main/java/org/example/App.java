package org.example;

import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public class App {

    // URI de conexión a eXistDB usando XML-RPC.
    // En este caso apunto directamente a la colección raíz /db,
    // tal y como se indica en el enunciado del ejercicio.
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/Formacion";

    // Credenciales de acceso a la base de datos.
    // Uso el usuario administrador para poder acceder a todas las colecciones.
    private static final String USER = "admin";
    private static final String PASSWORD = "";

    public static void main(String[] args) {

        // Variable donde guardaré la colección raíz (/db)
        // La inicializo a null para poder cerrarla correctamente en el finally.
        Collection rootCollection = null;

        try {
            // 1) Carga dinámica del driver XML:DB de eXistDB.
            // Con Class.forName() me aseguro de que la clase DatabaseImpl
            // se carga en memoria antes de intentar la conexión.
            Class<?> cl = Class.forName(DatabaseImpl.class.getName());

            // 2) Creo una instancia del driver y la registro en DatabaseManager.
            // Esto es necesario para que la API XML:DB sepa qué base de datos usar.
            Database database = (Database) cl.getDeclaredConstructor().newInstance();
            DatabaseManager.registerDatabase(database);

            // 3) Establezco la conexión con la base de datos y obtengo la colección raíz (/db).
            // Si las credenciales o la URI no son correctas, aquí se lanzará una excepción.
            rootCollection = DatabaseManager.getCollection(URI, USER, PASSWORD);

            // Compruebo que realmente se ha obtenido la colección.
            // Si es null, significa que no se ha podido conectar correctamente.
            if (rootCollection == null) {
                System.out.println("No se pudo obtener la colección raíz (/db). " +
                        "Revisa URI/credenciales o que eXistDB esté arrancado.");
                return;
            }

            // Si llego aquí, la conexión ha sido correcta.
            System.out.println("Conectado correctamente a: " + rootCollection.getName());

            // 4) Obtengo las subcolecciones que cuelgan de la colección raíz.
            // Este método devuelve un array de Strings con los nombres de las colecciones.
            String[] subCollections = rootCollection.listChildCollections();
            /**
             * Para una consulta más amplia y entrar a los documentos dentro de las colleciones sería:
             *             String[] resources = rootCollection.listResources();
             *             System.out.println("Documentos en la colección:");
             *
             *              for (String res : resources) {
             *              System.out.println("  " + res);}
             *
             */


            System.out.println("Subcolecciones en /db/Formacion:");

            // Si no hay subcolecciones, lo indico por consola.
            if (subCollections == null || subCollections.length == 0) {
                System.out.println("  (No hay subcolecciones)");
            } else {
                // Recorro el array y muestro cada subcolección encontrada.
                for (String col : subCollections) {
                    System.out.println("  /db/Formacion: " + col);
                }
            }

        } catch (ClassNotFoundException e) {
            // Este error se produce si no se encuentra el driver de eXistDB,
            // normalmente por un problema en las dependencias Maven.
            System.out.println("ERROR: No se encontró la clase DatabaseImpl. " +
                    "Revisa dependencias Maven.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (XMLDBException e) {
            // Este error engloba problemas de conexión, autenticación o consulta
            // dentro de la API XML:DB.
            System.out.println("ERROR XMLDB: No se pudo conectar o " +
                    "consultar la base de datos.");
            System.out.println("Código: " + e.errorCode);
            System.out.println("Detalle: " + e.getMessage());

        } catch (Exception e) {
            // Capturo cualquier otra excepción no prevista para evitar
            // que el programa termine de forma abrupta.
            System.out.println("ERROR general: " + e.getClass().getSimpleName());
            System.out.println("Detalle: " + e.getMessage());

        } finally {
            // 5) Cierre de la conexión.
            // Este bloque se ejecuta siempre, haya habido error o no,
            // asegurando que la colección se cierra correctamente.
            if (rootCollection != null) {
                try {
                    rootCollection.close();
                    System.out.println("Conexión cerrada correctamente.");
                } catch (XMLDBException e) {
                    System.out.println("Aviso: error al cerrar la colección.");
                    System.out.println("Detalle: " + e.getMessage());
                }
            }
        }
    }
}

