import java.sql.*;
import java.sql.SQLException;

/**
 *
 * @Author Alvaro Lozano
 * @since 21/10/2025
 */

public class ConsultaPREPARED {

    //Hacemos la conecxión a la base de datos
    //Para hacer la conexión necesitas las tres constantes:
    //URL, USER, PASSWORD
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";

    // Usuario y contraseña de MySQL
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        //Variable para el ejercico, buscamos el agente colocado en la posición 7
        int codigoAgenteBuscar = 7;

        //Los inicializamos a null para poder acceder en el bloque 'finally' y cerralos.
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            System.out.println("Conectando a la base de datos...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con éxito.");

            //---2º Preparar la consulta SQL---
            String sql = "SELECT CODIGO_AGENTE, NOMBRE_AGENTE, FRASE_CLAVE FROM agentes WHERE CODIGO_AGENTE = ?";

            //Creamos el objeto PreparedStatement a partir de la conexión y nuestro SQL
            preparedStatement = connection.prepareStatement(sql);

            //---3º Asignar valores a los Parámetros
            preparedStatement.setInt(1, codigoAgenteBuscar);

            //---4º Ejecutar la Consulta---
            System.out.println("Ejecutando consulta: " + preparedStatement.executeQuery()); //Mostramos la consulta y el ? se verá como 7

            ResultSet resultset = preparedStatement.executeQuery();

            System.out.println("Procesando resultados...");
            while (resultset.next()) {
                //Si entramos aquí es que hemos encontrado almenos una fila

                int codigoAgente = resultset.getInt("CODIGO_AGENTE");
                String nombreAgente = resultset.getString("NOMBRE_AGENTE");
                String fraseClave = resultset.getString("FRASE_CLAVE");

                //Mostramos la información recuperada por consola
                System.out.println("Agente Encontrado: ");
                System.out.println("Codigo Agente: " + codigoAgente);
                System.out.println("Nombre Agente: " + nombreAgente);
                System.out.println("Frase Clave: " + fraseClave);
            }

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
