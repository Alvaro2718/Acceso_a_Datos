import java.sql.*;
import java.sql.SQLException;

/**
 *
 * @Author Alvaro Lozano
 * @since 21/10/2025
 */

public class ConsultaSELECT {


    //Hacemos la conecxión a la base de datos
    //Para hacer la conexión necesitas las tres constantes:
    //URL, USER, PASSWORD
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";

    // Usuario y contraseña de MySQL
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        try( //Paso 1: Establecemos Conexión con la base de datos
             Connection co = DriverManager.getConnection(URL, USER, PASSWORD);

             //Paso 2: Creamos el objeto statement
             Statement s = co.createStatement())

        {//Aquí el código dentro del try-catch

            //Paso 3: Ejecutamos la consulta
            String sql = "SELECT CODIGO_AGENTE, NOMBRE_AGENTE, FRASE_CLAVE FROM agentes";

            //Con ResultSet almacenamos el resultado de la busqueda
            ResultSet resultSet = s.executeQuery(sql);

            //Paso 4: Procesar los datos por consola
            while(resultSet.next()){
                String codigoAgente = resultSet.getString("CODIGO_AGENTE");
                String nombreAgente = resultSet.getString("NOMBRE_AGENTE");
                String fraseClave = resultSet.getString("FRASE_CLAVE");
                System.out.println(codigoAgente + " " + nombreAgente + " " + fraseClave);
            }
        //Manejo Errores Sql
        }catch (SQLException e) {
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
    private static void muestraErrorSQL(SQLException e) {
        System.out.println("Error SQL:");
        System.out.println("Mensaje: " + e.getMessage());
        System.out.println("Estado SQL: " + e.getSQLState());
        System.out.println("Código del error: " + e.getErrorCode());
    }

}
