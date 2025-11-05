import java.sql.*; // Importamos todas las clases necesarias del paquete java.sql (Connection, PreparedStatement, ResultSet, SQLException)

/**
 * @author Álvaro Lozano
 * @since 21/10/2025
 */
public class ConsultaPREPARED {

    // --- Datos de conexión a la base de datos ---
    private static final String URL = "jdbc:mysql://localhost:3306/ut3"; // Dirección del servidor MySQL y base de datos
    private static final String USER = "root";                           // Usuario de MySQL
    private static final String PASSWORD = "123456";                     // Contraseña del usuario

    public static void main(String[] args) {

        // Variable que indica el código del agente que queremos buscar
        int codigoAgenteBuscar = 7;

        // Declaramos las variables JDBC necesarias
        Connection connection = null;         // Representa la conexión a la base de datos
        PreparedStatement preparedStatement = null; // Permite ejecutar consultas SQL con parámetros
        ResultSet resultSet = null;           // Contendrá los resultados devueltos por la consulta

        try {
            // --- 1️⃣ Conectamos con la base de datos ---
            System.out.println("Conectando a la base de datos...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD); // Establece la conexión con MySQL
            System.out.println("Conexión establecida con éxito.");

            // --- 2️⃣ Preparamos la consulta SQL ---
            // La consulta tiene un parámetro (?) que se sustituirá con un valor concreto
            String sql = "SELECT CODIGO_AGENTE, NOMBRE_AGENTE, FRASE_CLAVE FROM agentes WHERE CODIGO_AGENTE = ?";

            // Creamos el objeto PreparedStatement a partir de la conexión y la consulta SQL
            preparedStatement = connection.prepareStatement(sql);

            // --- 3️⃣ Sustituimos el parámetro de la consulta ---
            // El primer parámetro (?) se reemplaza por el valor de codigoAgenteBuscar
            preparedStatement.setInt(1, codigoAgenteBuscar);

            System.out.println("Ejecutando consulta con código de agente: " + codigoAgenteBuscar);

            // --- 4️⃣ Ejecutamos la consulta ---
            // executeQuery() ejecuta el SELECT y devuelve un ResultSet con los resultados
            resultSet = preparedStatement.executeQuery();

            System.out.println("Procesando resultados...");
            boolean encontrado = false; // Variable para saber si encontramos algún agente

            // --- 5️⃣ Recorremos el ResultSet para leer los datos obtenidos ---
            while (resultSet.next()) { // next() avanza a la siguiente fila (devuelve false si no hay más)
                encontrado = true;

                // Obtenemos los valores de las columnas del agente
                int codigoAgente = resultSet.getInt("CODIGO_AGENTE");
                String nombreAgente = resultSet.getString("NOMBRE_AGENTE");
                String fraseClave = resultSet.getString("FRASE_CLAVE");

                // Mostramos los datos del agente por consola
                System.out.println("Agente encontrado:");
                System.out.println("Código Agente: " + codigoAgente);
                System.out.println("Nombre Agente: " + nombreAgente);
                System.out.println("Frase Clave: " + fraseClave);
            }

            // --- 6️⃣ Si no se encontró ningún agente ---
            if (!encontrado) {
                System.out.println("No se encontró ningún agente con el código " + codigoAgenteBuscar);
            }

            // --- 7️⃣ Capturamos los errores SQL ---
        } catch (SQLException e) {
            muestraErrorSQL(e);

            // --- 8️⃣ Capturamos cualquier otro error no relacionado con SQL ---
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());

            // --- 9️⃣ Cerramos los recursos en el bloque finally ---
        } finally {
            try {
                // Cerramos el ResultSet si fue abierto
                if (resultSet != null) resultSet.close();
                // Cerramos el PreparedStatement si fue abierto
                if (preparedStatement != null) preparedStatement.close();
                // Cerramos la conexión si fue abierta
                if (connection != null) {
                    connection.close();
                    System.out.println("Conexión cerrada correctamente.");
                }

            } catch (SQLException e) {
                // Si ocurre un error al cerrar los recursos, también lo mostramos
                muestraErrorSQL(e);
            }
        }
    }

    /**
     * --- Método auxiliar ---
     * Muestra información detallada sobre cualquier error SQL que ocurra.
     * Nos permite depurar el problema de forma más precisa.
     */
    private static void muestraErrorSQL(SQLException e) {
        System.out.println(" Error SQL:");
        System.out.println(" Mensaje: " + e.getMessage());      // Mensaje de error del motor SQL
        System.out.println(" Estado SQL: " + e.getSQLState());  // Código de estado SQL (estándar)
        System.out.println(" Código del error: " + e.getErrorCode()); // Código específico del error (propio de MySQL)
        e.printStackTrace(); // Muestra la traza completa del error (opcional para depuración)
    }
}
