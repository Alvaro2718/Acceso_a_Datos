

import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import java.lang.reflect.InvocationTargetException;

public class ConsultaProfesoresXQJ {

    public static void main(String[] args) {

        XQConnection conn = null;
        XQExpression expr = null;
        XQResultSequence rs = null;

        try {
            // PASO 1 (según el cheatsheet): cargo el driver XQJ de eXistDB por reflexión
            // Si falta algún JAR (exist-xqj, xqj2 o xqjapi), aquí es donde suele fallar.
            XQDataSource xqs = (XQDataSource)
                    Class.forName("net.xqj.exist.ExistXQDataSource")
                            .getDeclaredConstructor()
                            .newInstance();

            // PASO 2: configuro los parámetros de conexión con setProperty()
            // Básicamente hago lo mismo que con JDBC, pero adaptado a XQJ.
            xqs.setProperty("serverName", "localhost");
            xqs.setProperty("port", "8080");
            xqs.setProperty("user", "admin");
            xqs.setProperty("password", ""); // si tienes contraseña, la pones aquí

            // PASO 3: obtengo la conexión
            conn = xqs.getConnection();

            // Compruebo de forma simple que la conexión está abierta
            if (conn == null || conn.isClosed()) {
                System.out.println("No se pudo abrir la conexión con eXistDB usando XQJ.");
                return;
            }

            System.out.println("Conexión XQJ establecida correctamente.");

            // PASO 4: creo una expresión para ejecutar consultas XQuery
            expr = conn.createExpression();

            // PASO 5: defino la consulta XQuery.
            // Aquí localizo el documento JesusLozano.xml dentro de /db/Formacion/Profesores
            // y saco todos sus datos (el documento completo).
            String xquery =
                    "doc('/base de datos/Formación/Profesores/JesúsLozano.xml')";

            // Si en lugar del documento completo quieres campos concretos, por ejemplo:
            // String xquery = "doc('/db/Formacion/Profesores/JesusLozano.xml')/profesor/nombre/text()";

            // PASO 6: ejecuto la consulta y recorro el resultado
            rs = expr.executeQuery(xquery);

            System.out.println("\nResultado de la consulta (JesusLozano.xml):\n");

            // En XQJ siempre tengo que hacer rs.next() antes de leer el item
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                System.out.println(rs.getItemAsString(null));
            }

            if (!hayResultados) {
                System.out.println("(La consulta no devolvió resultados. Revisa la ruta del doc() o que exista el XML.)");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No se encontró el driver XQJ (ExistXQDataSource). Revisa que añadiste los JARs.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("ERROR: No se pudo instanciar el driver XQJ mediante reflexión.");
            System.out.println("Detalle: " + e.getMessage());

        } catch (XQException e) {
            System.out.println("ERROR XQJ/XQuery: fallo en la conexión o en la consulta.");
            System.out.println("Detalle: " + e.getMessage());

        } finally {
            // PASO 7: cierro recursos en orden inverso (resultados → expresión → conexión)
            // Lo hago aquí para asegurarme de que se cierran aunque haya errores.
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
