import javax.swing.*;
import java.sql.*;

/**
 * @Author Alvaro
 * @since 1/11/2025
 */

public class Importacion_y_Consulta {

    // ---Datos para la conexión con la base de datos ---
    // URL: dirección del servidor MySQL + el nombre de la base de datos (ut3)
    private static final String URL = "jdabc:mysql://localhost:3306/ut3";

    // Usuario y contraseña de MySQL
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        // Sentencias SQL para crear la tabla "Datos Climáticos"
        String sqlCreate = "CREATE TABLE Datos_Climaticos (" +
                "ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT," + // añadimos el la columna ID para tener una clave
                                                                // primaria única
                "PROVINCIA VARCHAR(45) NOT NULL," +
                "ESTACION VARCHAR(45) NULL," +
                "FECHA VARCHAR(45) NULL," +
                "TEMPERATURA DECIMAL(5,2) NULL," + // Usamos DECIMAL(5,2) cuando queremos mayor precisión decimal, algo
                                                   // importante para datos meteorológicos
                "HUMEDAD DECIMAL(5,2) NULL," +
                "PRECIPITACION DECIMAL(5,2) NULL" +
                ")";

        // Conexión y ejecución de la sentencia SQL
        // El bloque try-catch resource cierra automáticamente los recursos (conexión y
        // statement)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()) {

            // Ejecuta la sentencia SQL de creación de la tabla
            statement.executeUpdate(sqlCreate);
            System.out.println("Tabla 'Datos_Climaticos' creada correctamente.");

            // Llamada al método de hacer la consulta para temperatura media
            consultarPrecipitacionMedia(connection, "Algemesi");

            // Llamada al método del procedimiento, que consulta la precipitación total de
            // una estación
            calcularPrecipitacionTotal(connection, "Algemesi");

        } catch (SQLException e) { // Para el manejo de errores SQl
            muestraErrorSQL(e); // Llamamos al metodo 'muestraErroSQL

        } catch (Exception e) { // Para el manejo de errores generales
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }// Cerramos el método main

    private static void muestraErrorSQL(SQLException e) {
        System.out.println("Error SQL:");
        System.out.println("Mensaje: " + e.getMessage());
        System.out.println("Estado SQL: " + e.getSQLState());
        System.out.println("Código del error: " + e.getErrorCode());
    }

    // Método que hace una consulta de cálcular la temperatura média de una estación
    // usando 'ResulSet'

    private static void consultarPrecipitacionMedia(Connection connection, String estacion) {
        String sqlQuery = "SELECT AVG(Precipitacion) AS MediaPrecipitacion " +
                "FROM Datos_Climaticos " +
                "WHERE Estacion='" + estacion + "'";

        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sqlQuery)) {

            if (rs.next()) {
                double media = rs.getDouble("MediaPrecipitacion");
                System.out.println("La precipitación media en " + estacion + " es: " + media);
                JOptionPane.showMessageDialog(null,
                        "La precipitación media en " + estacion + " es: " + media,
                        "Precipitación Media",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            muestraErrorSQL(e);
        }
    }

    // Método para calcular la precipitación total
    private static void calcularPrecipitacionTotal(Connection connection, String estacion) {
        String sqlCall = "{CALL CalcularPrecipitacionTotal(?, ?)}";

        try (CallableStatement cs = connection.prepareCall(sqlCall)) {
            // Parámetro de entrada
            cs.setString(1, estacion);
            // Parámetro de salida
            cs.registerOutParameter(2, java.sql.Types.DECIMAL);

            // Ejecutar el procedimiento
            cs.execute();

            // Obtener el valor de salida
            double total = cs.getDouble(2);

            System.out.println("La precipitación total en " + estacion + " es: " + total);
            JOptionPane.showMessageDialog(null,
                    "La precipitación total en " + estacion + " es: " + total,
                    "Precipitación Total",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            muestraErrorSQL(e);
        }
    }

}
// Crear el procedimiento
// En phpMyAdmin, selecciona tu base de datos ut3 y ejecuta esta sentencia SQL:
/**
 *
 * DELIMITER //
 *
 * CREATE PROCEDURE CalcularPrecipitacionTotal(IN estacionNombre VARCHAR(45),
 * OUT totalPrecipitacion DECIMAL(10,2))
 * BEGIN
 * SELECT SUM(Precipitacion) INTO totalPrecipitacion
 * FROM Datos_Climaticos
 * WHERE Estacion = estacionNombre;
 * END //
 *
 * DELIMITER ;
 */

// Esto crea un procedimiento que recibe el nombre de la estación y devuelve la
// precipitación total en octubre.
