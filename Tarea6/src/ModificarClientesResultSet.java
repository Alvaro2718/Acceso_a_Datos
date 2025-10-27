import javax.swing.*;
import java.sql.*;
/**
 * @Author Alvaro Lozano
 *
 * @since 23/10/2025
 */

public class ModificarClientesResultSet {

    //Constantes que usaré para la conexión a mi base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";

    private static final String USER = "root";
    private static final String PASSWORD = "123456";


    public static void main(String[] args) {


        // Conexión y ejecución de la sentencia SQL
        // El bloque try-with-resources cierra automáticamente los recursos (conexión y statement)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement s = connection.createStatement()) {
            System.out.println("La conexión a sido exitosa.");

            //Deshabilitar auto-commint para manejo de transacciones
            connection.setAutoCommit(false);

            // Usamos otro 'Try-with-resources' para el Statement
            try(Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                ResultSet resultSet = st.executeQuery("SELECT DNI, APELLIDOS, CP FROM Clientes");

                //Iniciamos un contador para saber cuantas filas modifico
                int filasModificadas = 0;
                while (resultSet.next()) {
                    String cpActual = resultSet.getString("CP");

                    //Para comnprobar que CP no tenga valor NULL
                    if(cpActual == null){
                        //1. Actualización en el buffer del ResultSet:
                        resultSet.updateString("CP", "0000");
                        resultSet.updateRow();
                        filasModificadas++;
                    }
                }
                //Para que se hagan los camibos en la base de datos tenemos que hacer un COMMIT
                connection.commit();

                //Mostrar número de filas modificadas en cuadro de diálogo
                JOptionPane.showMessageDialog(null,filasModificadas + " clientes modificados exitosamente.",
                        "Resultado de la Modificación",
                        JOptionPane.INFORMATION_MESSAGE);


            }catch(SQLException e){

                //(Opcional: Si algo falla, deberíamos deshacer la transacción
                //para evitar cambios parciales).
                //connection.rollback
                muestraErrorSQL(e);
            }



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
