import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Alvaro Lozano
 *
 * @since 20/10/2025
 */
public class CreateTable {

    // --- Datos de conexión con la base de datos ---
    // URL: dirección del servidor MySQL + nombre de la base de datos (ut3)

    private static final String URL = "jdbc:mysql://localhost:3306/ut3";

    // Usuario y contraseña de MySQL
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        // Sentencia SQL para crear la tabla 'Clientes'
        // IF NOT EXISTS evita error si la tabla ya existe
        String sqlCreate = "CREATE TABLE Clientes (" +
                "DNI CHAR(9) NOT NULL PRIMARY KEY," + // Clave primaria, no puede ser nula
                "APELLIDOS VARCHAR(32) NOT NULL," + // Campo obligatorio de texto
                "CP CHAR(5) NULL" + // Campo opcional (puede ser nulo)
                ")";

        // Conexión y ejecución de la sentencia SQL
        // El bloque try-with-resources cierra automáticamente los recursos (conexión y
        // statement)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()) {

            // Ejecuta la sentencia SQL de creación de la tabla
            statement.executeUpdate(sqlCreate);
            System.out.println("Tabla 'Clientes' creada correctamente.");

            // Manejo de errores SQL
        } catch (SQLException e) {
            muestraErrorSQL(e);

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
