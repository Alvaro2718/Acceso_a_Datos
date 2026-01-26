import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class RepasoDocumentos {

    //  REUTILIZACIÓN:
    // Cambia la URI para apuntar a la colección donde te pidan trabajar.
    // Ejemplo: "/db/Formacion/Profesores" o "/db/Empresa/Empleados"
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/Formacion/Alumnos";
    private static final String USER = "admin";
    private static final String PASS = "";

    public static void main(String[] args) {

        Collection col = null;

        try {
            // 1) Driver (si en el examen os piden DRIVER como String, lo puedes poner en constante)
            Class<?> cl = Class.forName(DatabaseImpl.class.getName());
            Database db = (Database) cl.getDeclaredConstructor().newInstance();
            DatabaseManager.registerDatabase(db);

            // 2) Conectar a la colección (IMPORTANTE: si no existe -> col será null)
            col = DatabaseManager.getCollection(URI, USER, PASS);
            if (col == null) {
                System.out.println("La colección no existe: " + URI);
                return;
            }

            // 3) Mostrar documentos existentes (listResources = lista de nombres de ficheros XML)
            mostrarDocumentos(col);

            // REUTILIZACIÓN:
            // Creamos el nombre de los documentos ( se puden sustituir)
            String docName1 = "profesor1.xml";
            String docName2 = "profesor2.xml";
            String docName3 = "profesor3.xml";

            // REUTILIZACIÓN ( Creamos el contenido FORMATO XML):
            // OJO: comillas en atributos -> \"
            String xml1 =
                    "<profesor id=\"1\">\n" +
                            "  <nombre>Alvaro Lozano</nombre>\n" +
                            "  <dni>639262318</dni>\n" +
                            "  <email>lozano.alvaro@dominiox.es</email>\n" +
                            "</profesor>";

            String xml2 =
                    "<profesor id=\"2\">\n" +
                            "  <nombre>Ana Pérez</nombre>\n" +
                            "  <dni>12345678A</dni>\n" +
                            "  <email>ana.perez@dominiox.es</email>\n" +
                            "</profesor>";

            String xml3 =
                    "<profesor id=\"3\">\n" +
                            "  <nombre>Carlos Ruiz</nombre>\n" +
                            "  <dni>87654321B</dni>\n" +
                            "  <email>carlos.ruiz@dominiox.es</email>\n" +
                            "</profesor>";

            // 4) Crear documentos si no existen (getResource + createResource + storeResource)
            crearDocumentoSiNoExiste(col, docName1, xml1);
            crearDocumentoSiNoExiste(col, docName2, xml2);
            crearDocumentoSiNoExiste(col, docName3, xml3);

            // 5) Mostrar otra vez para comprobar que se han creado
            mostrarDocumentos(col);

            // REUTILIZACIÓN:
            // Borra el documento que te pidan. (removeResource)
            borrarDocumentoSiExiste(col, docName1);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());

        } finally {
            // 6) Cierre seguro (siempre en finally)
            try { if (col != null) col.close(); } catch (Exception ignored) {}
        }
    }

    // ✅ REUTILIZACIÓN:
    // Este método sirve para cualquier colección: lista todos los XML dentro.
    private static void mostrarDocumentos(Collection col) throws XMLDBException {
        System.out.println("Documentos en la colección:");
        String[] docs = col.listResources();

        if (docs == null || docs.length == 0) {
            System.out.println("  (No hay documentos)");
            return;
        }

        for (String d : docs) {
            System.out.println("  " + d);
        }
    }

    // ✅ REUTILIZACIÓN:
    // Crear documento XML si no existe: patrón típico de examen.
    private static void crearDocumentoSiNoExiste(
            Collection col, String name, String content) throws XMLDBException {

        // Si existe, no lo creo
        if (col.getResource(name) != null) {
            System.out.println("Ya existe: " + name);
            return;
        }

        // Creo el recurso XML en memoria, le pongo el contenido y lo guardo en eXistDB
        XMLResource res = (XMLResource) col.createResource(name, "XMLResource");
        res.setContent(content);
        col.storeResource(res);

        System.out.println("Creado: " + name);
    }

    // ✅ REUTILIZACIÓN:
    // Borrar documento si existe: también muy típico.
    private static void borrarDocumentoSiExiste(
            Collection col, String name) throws XMLDBException {

        Resource res = col.getResource(name);
        if (res == null) {
            System.out.println("No existe: " + name);
            return;
        }

        col.removeResource(res);
        System.out.println("Borrado: " + name);
    }
}
