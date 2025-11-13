import javax.swing.*;
import java.sql.*;

/**
 * @Author Alvaro Lozano
 *
 * @since 23/10/2025
 */

public class JDBCinsert {

    private static final String URL = "jdbc:mysql://localhost:3306/ut3";

    private static final String USER = "root";
    private static final String PASSWORD = "123456";


    public static void main(String[] args) {


        // Conexión y ejecución de la sentencia SQL
        // El bloque try-with-resources cierra automáticamente los recursos (conexión y statement)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement s = connection.createStatement()) {
            System.out.println("La conexión a sido exitosa.");

            //Insertamos una FILA en la base de datos
            int nFil = s.executeUpdate(
                    "INSERT INTO Clientes (DNI, APELLIDOS, CP) VALUES " +
                            "('78901234W' , 'BLEDA', '44006');"
            );

            //4º MOSTRAR EL RESULTADO EN UN CUADRO DE DIÁLOGO
            JOptionPane.showMessageDialog(null, nFil + "filas insertadas.", "Resultado de la inserción", JOptionPane.INFORMATION_MESSAGE);

            // Manejo de errores SQL
        } catch (SQLException e) {
            muestraErrorSQL(e);

            //Manejo de errores generales
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

    }

    /**
     * Método para mostrar información detallada sobre los errores SQL
     * Muestra el mensaje, el estado SQL y el código de error del proveedor.
     */
    private static void muestraErrorSQL (SQLException e){
        System.out.println("Error SQL:");
        System.out.println("Mensaje: " + e.getMessage());
        System.out.println("Estado SQL: " + e.getSQLState());
        System.out.println("Código del error: " + e.getErrorCode());
    }
}

