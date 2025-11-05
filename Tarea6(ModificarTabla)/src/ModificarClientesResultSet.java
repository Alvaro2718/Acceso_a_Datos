import java.sql.*;
import javax.swing.JOptionPane;

/**
 * @author Álvaro Lozano
 * @since 21/10/2025
 *
 * Ejercicio: Modificar los registros de la tabla CLIENTES usando ResultSet actualizable.
 */
public class ModificarClientesResultSet {

    // --- Datos de conexión ---
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        int filasModificadas = 0;

        // --- try-with-resources para conexión y statement ---
        try (
                // Paso 1: Establecer conexión con la base de datos
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

                // Paso 2: Crear Statement con ResultSet actualizable y desplazable
                Statement statement = connection.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,    // permite moverse adelante/atrás y detectar cambios
                        ResultSet.CONCUR_UPDATABLE          // permite modificar datos directamente
                );

                // Paso 3: Ejecutar la consulta (seleccionamos todos los clientes)
                ResultSet resultSet = statement.executeQuery("SELECT DNI, APELLIDOS, CP FROM CLIENTES");
        ) {

            // --- Deshabilitar auto-commit para controlar la transacción manualmente ---
            connection.setAutoCommit(false);

            System.out.println("Conexión establecida correctamente.");
            System.out.println("Procesando registros...");

            // --- Recorrer las filas y actualizar los valores nulos en CP ---
            while (resultSet.next()) {
                String codigoPostal = resultSet.getString("CP");

                if (codigoPostal == null) {
                    resultSet.updateString("CP", "00000");
                    resultSet.updateRow();
                    filasModificadas++;
                }
            }

            // --- Confirmar los cambios ---
            connection.commit();

            // --- Mostrar resultado gráfico ---
            JOptionPane.showMessageDialog(null,
                    "Número de registros actualizados: " + filasModificadas,
                    "Actualización completada",
                    JOptionPane.INFORMATION_MESSAGE);

            System.out.println("Registros modificados: " + filasModificadas);

        } catch (SQLException e) {
            muestraErrorSQL(e); //Llamada al metodo que gestiona errores SQL
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Muestra información detallada sobre un error SQL
     */
    private static void muestraErrorSQL(SQLException e) {
        System.out.println("  Error SQL:");
        System.out.println("  Mensaje: " + e.getMessage());
        System.out.println("  Estado SQL: " + e.getSQLState());
        System.out.println("  Código de error: " + e.getErrorCode());
        e.printStackTrace();
    }
}
