import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conector {

    // --- Datos de conexión (ajústalos a tu base de datos real) ---
    //private static final String URL = "jdbc:mysql://localhost:3306/phpmyadmin?useSSL=false&serverTimezone=UTC";
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";

    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    // --------------------------------------------------------------

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Intentar establecer la conexión con la base de datos
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión a la base de datos establecida correctamente.");

            // Llamamos a una función extra para demostrar que la conexión funciona
            ejecutarConsultaPrueba(connection);

        } catch (SQLException e) {
            System.out.println("❌ Error al conectar a la base de datos.");
            System.out.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Cerrar la conexión, asegurándonos de que se cierre siempre
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("🔒 Conexión cerrada correctamente.");
                } catch (SQLException ex) {
                    System.out.println("⚠️ Error al cerrar la conexión: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     *  extra para obtener el 10
     * Metodo que ejecuta una consulta simple para comprobar que la conexión funciona
     */
    private static void ejecutarConsultaPrueba(Connection connection) {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Crear un objeto Statement para ejecutar consultas SQL
            statement = connection.createStatement();

            // Ejecutar una consulta simple que devuelva el número 1
            resultSet = statement.executeQuery("SELECT 1 AS resultado");

            // Mostrar el resultado en la consola
            if (resultSet.next()) {
                int resultado = resultSet.getInt("resultado");
                System.out.println("🔎 Resultado de la consulta de prueba: " + resultado);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta de prueba: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Cerrar el ResultSet y el Statement
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
}
