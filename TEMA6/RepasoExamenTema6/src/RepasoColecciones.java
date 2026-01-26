import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

public class CrearColeccionesDB {

    //  REUTILIZACIÓN: cambia aquí el servidor/puerto/colección raíz si te lo piden
    private static final String URI_DB = "xmldb:exist://localhost:8080/exist/xmlrpc/db";
    private static final String USER = "admin";
    private static final String PASS = "";

    public static void main(String[] args) {

        Collection db = null;
        Collection formacion = null;

        try {
            // 1) Driver
            Class<?> cl = Class.forName(DatabaseImpl.class.getName());
            Database database = (Database) cl.getDeclaredConstructor().newInstance();
            DatabaseManager.registerDatabase(database);

            // 2) Conectar a /db (colección padre)
            db = DatabaseManager.getCollection(URI_DB, USER, PASS);
            if (db == null) {
                System.out.println("No se pudo acceder a /db");
                return;
            }

            System.out.println("Conectado correctamente a la colección: " + db.getName());

            // 3) Servicio en /db
            CollectionManagementService cmsDB =
                    (CollectionManagementService) db.getService("CollectionManagementService", "1.0");

            //  REUTILIZACIÓN: cambia los nombres por los que te pidan
            crearColeccionSiNoExiste(db, cmsDB, "Formacion");
            crearColeccionSiNoExiste(db, cmsDB, "Empresa");
            crearColeccionSiNoExiste(db, cmsDB, "Pruebas");

            // 4) Mostrar hijas de /db
            mostrarColeccionesHijas(db);

            // 5) Abrir /db/Formacion para operar dentro (crear/listar/borrar subcolecciones)
            // Para entrar en subcolecciones, Solo cambia "/Formacion"
            formacion = DatabaseManager.getCollection(URI_DB + "/Formacion", USER, PASS);
            if (formacion != null) {
                mostrarColeccionesHijas(formacion);

                // 6) BORRAR colecciones (ejemplo)
                // ✅ REUTILIZACIÓN: aquí borras la que te pidan (hija del "padre" que pases)
                // OJO: removeCollection borra la colección y su contenido.
                CollectionManagementService cmsFormacion =
                        (CollectionManagementService) formacion.getService("CollectionManagementService", "1.0");

                // Ejemplo: borrar /db/Formacion/Temp si existiera
                eliminarColeccionSiExiste(formacion, cmsFormacion, "Temp");
            }

            // 7) BORRAR colecciones hijas directas de /db (ejemplo)
            // Ejemplo: borrar /db/Pruebas si existe
            eliminarColeccionSiExiste(db, cmsDB, "Pruebas");

            // Comprobar cambios
            mostrarColeccionesHijas(db);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());

        } finally {
            // Cierre seguro
            try { if (formacion != null) formacion.close(); } catch (Exception ignored) {}
            try { if (db != null) db.close(); } catch (Exception ignored) {}
        }
    }

    /** Crea una subcolección si no existe. */
    private static void crearColeccionSiNoExiste(
            Collection padre,
            CollectionManagementService cms,
            String nombre) throws XMLDBException {

        if (existeSubcoleccion(padre, nombre)) {
            System.out.println("Ya existe: /" + padre.getName() + "/" + nombre);
            return;
        }

        cms.createCollection(nombre);
        System.out.println("Creada: /" + padre.getName() + "/" + nombre);
    }

    /** Elimina una subcolección si existe. */
    private static void eliminarColeccionSiExiste(
            Collection padre,
            CollectionManagementService cms,
            String nombre) throws XMLDBException {

        if (!existeSubcoleccion(padre, nombre)) {
            System.out.println("No existe (no se borra): /" + padre.getName() + "/" + nombre);
            return;
        }

        cms.removeCollection(nombre);
        System.out.println("Borrada: /" + padre.getName() + "/" + nombre);
    }

    /** Comprueba si existe una subcolección dentro del padre. */
    private static boolean existeSubcoleccion(Collection padre, String nombre)
            throws XMLDBException {

        String[] hijas = padre.listChildCollections();
        if (hijas == null) return false;

        for (String h : hijas) {
            if (h.equals(nombre)) return true;
        }
        return false;
    }

    /** Muestra subcolecciones hijas. */
    private static void mostrarColeccionesHijas(Collection padre)
            throws XMLDBException {

        System.out.println("Colecciones hijas de /" + padre.getName() + ":");

        String[] hijas = padre.listChildCollections();
        if (hijas == null || hijas.length == 0) {
            System.out.println("  (No hay subcolecciones)");
            return;
        }

        for (String h : hijas) {
            System.out.println("  /" + padre.getName() + "/" + h);
        }
    }
}
