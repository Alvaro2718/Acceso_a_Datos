import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Alvaro Lozano
 *
 * @since 6/11/2025
 */

public class Examen1 {

    // Conexión con la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args){


        // Sentencia SQL para crear la tabla 'examen'
        // IF NOT EXISTS evita error si la tabla ya existe
        String sqlCreate = "CREATE TABLE examen (" + //nombre de la tabla que queremos crear
                "DNI CHAR(9) NOT NULL PRIMARY KEY," + // Clave primaria, no puede ser nula
                "APELLIDOS VARCHAR(32) NOT NULL," + // Apellidos, campo con de texto obligatorio
                "NOTAS DECIMAL(5,2) NOT NULL" + // Notas con un número de hasta 5 dígitos y un decimal de 2 números, obligatorio
                ")";

        // Conexión y ejecución de la sentencia SQL
        // El bloque try-with-resources cierra automáticamente los recursos utilizados para la conexión
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement s = connection.createStatement()) {

            // Ejecuta la sentencia SQL de creación de la tabla
            s.executeUpdate(sqlCreate);
            System.out.println("Tabla 'examen' creada correctamente.");

            //Insertamos una FILA en la base de datos
            int nFil = s.executeUpdate(
                    "INSERT INTO examen (DNI, APELLIDOS, NOTA) VALUES " +
                            "('77723879W' , 'LOZANO', '8.8');"
            );

            // Manejo de errores SQL
        } catch (SQLException e) {
            muestraErrorSQL(e); // Llamada a nuestro método de gestionar error SQL

            // Manejo de errores generales
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
    /**
     * Método para mostrar información detallada sobre los errores SQL
     * Muestra el mensaje, el estado SQL y el código de error del proveedor.
     */
    private static void muestraErrorSQL(SQLException e) {
        System.out.println("Error SQL:");
        System.out.println("Mensaje: " + e.getMessage());
        System.out.println("Estado SQL: " + e.getSQLState());
        System.out.println("Código del error: " + e.getErrorCode());
    }

}
