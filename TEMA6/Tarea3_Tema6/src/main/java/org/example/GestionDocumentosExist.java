package org.example;

import java.lang.reflect.InvocationTargetException;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class GestionDocumentosExist {

    // Constantes de conexión
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/Formacion/Profesores";
    private static final String USER = "admin";
    private static final String PASS = "";

    public static void main(String[] args) {

        Collection formacionCol = null;

        try {
            // 1) Cargar el driver de eXistDB
            Class<?> cl = Class.forName(DRIVER);

            // 2) Instanciar el driver
            Database database = (Database) cl.getDeclaredConstructor().newInstance();

            // 3) Registrar el driver
            DatabaseManager.registerDatabase(database);

            // 4) Obtener la colección /db/Formacion
            formacionCol = DatabaseManager.getCollection(URI, USER, PASS);

            if (formacionCol == null) {
                System.out.println("No se pudo acceder a /db/Formacion. Comprueba que la colección existe y las credenciales.");
                return;
            }

            System.out.println("Conectado correctamente a: " + formacionCol.getName());

            // 5) Crear documentos (resources) en la colección
            String docName1 = "Alvaro.xml";
            String docName2 = "profesor.xml";

            String docContent1 =
                    "<profesor id=\"5\">\n" +
                            "  <nombre>Alvaro Lozano</nombre>\n" +
                            "  <dni>639262318</dni>\n" +
                            "  <email>lozano.alvaro@dominiox.es</email>\n" +
                            "</profesor>";

            String docContent2 =
                    "<profesor id=\"6\">\n" +
                            "  <nombre>Profesor6</nombre>\n" +
                            "  <dni>55589746w</dni>\n" +
                            "  <email>profesor6@dominiox.es</email>\n" +
                            "</profesor>";

            //crearDocumentoSiNoExiste(formacionCol, docName1, docContent1);
            crearDocumentoSiNoExiste(formacionCol, docName2, docContent2);

            // 6  eliminar un documento de prueba
            //eliminarDocumentoSiExiste(formacionCol, docName2);

        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No se encontró el driver (" + DRIVER + "). Revisa dependencias Maven.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("ERROR: No se pudo instanciar el driver de eXistDB.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (XMLDBException e) {
            System.out.println("ERROR XMLDB: Fallo en operación con la base de datos.");
            System.out.println("Código: " + e.errorCode);
            System.out.println("Detalle: " + e.getMessage());

        } finally {
            // 7) Cerrar conexión
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
     * Crea un documento XML dentro de la colección si no existe ya.
     */
    private static void crearDocumentoSiNoExiste(Collection col, String docName, String xmlContent) throws XMLDBException {

        // Si getResource no devuelve null, el documento ya existe
        Resource existing = col.getResource(docName);
        if (existing != null) {
            System.out.println("El documento " + docName + " ya existe (no lo vuelvo a crear).");
            return;
        }

        // Creo un recurso XML con nombre docName
        XMLResource res = (XMLResource) col.createResource(docName, "XMLResource");
        res.setContent(xmlContent);

        // Lo guardo en la colección
        col.storeResource(res);

        System.out.println("Documento creado correctamente: /db/Formacion/Profesores" + docName);
    }

    /**
     * Elimina un documento dentro de la colección si existe.
     */
    private static void eliminarDocumentoSiExiste(Collection col, String docName) throws XMLDBException {

        Resource existing = col.getResource(docName);
        if (existing == null) {
            System.out.println("El documento " + docName + " no existe (no se puede borrar).");
            return;
        }

        col.removeResource(existing);
        System.out.println("Documento eliminado correctamente: /db/Formacion/Profesores" + docName);
    }
}
