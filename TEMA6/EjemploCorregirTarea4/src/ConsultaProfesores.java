import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import java.lang.reflect.InvocationTargetException;

public class ConsultaProfesores {

    public static void main(String[] args) {

        // Declaro estas variables fuera del try para poder cerrarlas correctamente
        // en el bloque finally, independientemente de si ocurre algún error.
        XQConnection conn = null;
        XQExpression expr = null;
        XQResultSequence rs = null;

        try {
            // PASO 1:
            // Cargo e instancio el driver XQJ de eXistDB usando reflexión.
            // Si los JARs no están bien añadidos al proyecto, el error salta aquí.
            XQDataSource xqs = (XQDataSource)
                    Class.forName("net.xqj.exist.ExistXQDataSource")
                            .getDeclaredConstructor()
                            .newInstance();

            // PASO 2:
            // Configuro los parámetros de conexión a la base de datos eXistDB.
            // Es parecido a configurar una conexión JDBC, pero usando XQJ.
            xqs.setProperty("serverName", "localhost");
            xqs.setProperty("port", "8080");
            xqs.setProperty("user", "admin");
            xqs.setProperty("password", ""); // en mi caso no tengo contraseña

            // PASO 3:
            // Obtengo la conexión con la base de datos.
            conn = xqs.getConnection();

            // Compruebo que la conexión se ha establecido correctamente
            if (conn == null || conn.isClosed()) {
                System.out.println("No se pudo abrir la conexión con eXistDB usando XQJ.");
                return;
            }

            System.out.println("Conexión XQJ establecida correctamente.");

            // PASO 4:
            // A partir de la conexión creo una expresión XQuery,
            // que es el objeto que me permite ejecutar consultas.
            expr = conn.createExpression();

            // PASO 5:
            // Defino la consulta XQuery.
            // En este caso localizo el documento JesusLozano.xml dentro de la colección
            // /db/Formacion/Profesores y obtengo los datos del nodo <profesor>.
            String xquery =
                    "doc('/db/Formacion/Profesores/JesusLozano.xml')/profesor";

            // PASO 6:
            // Ejecuto la consulta y obtengo el resultado en un XQResultSequence.
            rs = expr.executeQuery(xquery);

            System.out.println("\nResultado de la consulta (JesusLozano.xml):\n");

            // Recorro los resultados.
            // En XQJ siempre hay que llamar primero a next() antes de leer el valor.
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                System.out.println(rs.getItemAsString(null));
            }

            // Si no se obtiene ningún resultado, muestro un mensaje informativo
            if (!hayResultados) {
                System.out.println("(La consulta no devolvió resultados. Revisa la ruta del documento o su contenido.)");
            }

        } catch (ClassNotFoundException e) {
            // Este error suele aparecer si no se han añadido correctamente
            // los JARs necesarios de XQJ al proyecto.
            System.out.println("ERROR: No se encontró el driver XQJ (ExistXQDataSource).");
            System.out.println("Detalle: " + e.getMessage());

        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            // Errores relacionados con la creación del driver mediante reflexión
            System.out.println("ERROR: No se pudo instanciar el driver XQJ.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (XQException e) {
            // Errores propios de la API XQJ:
            // problemas de conexión, consulta incorrecta, documento no encontrado, etc.
            System.out.println("ERROR XQJ/XQuery: fallo en la conexión o en la consulta.");
            System.out.println("Detalle: " + e.getMessage());

        } finally {
            // PASO 7:
            // Cierro todos los recursos en el bloque finally para asegurarme
            // de que se liberan aunque ocurra algún error durante la ejecución.
            try {
                if (rs != null) rs.close();
            } catch (XQException ignored) {}

            try {
                if (expr != null) expr.close();
            } catch (XQException ignored) {}

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("\nConexión cerrada correctamente.");
                }
            } catch (XQException e) {
                System.out.println("Aviso: error al cerrar la conexión.");
                System.out.println("Detalle: " + e.getMessage());
            }
        }
    }
}

