import java.sql.*;

/**
 * @author Alvaro Lozano
 * @since 6/11/2025
 */

public class Examen2 {

    // Conexión con la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/ut3";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        // Declaramos las variables JDBC necesarias
        PreparedStatement preparedStatement = null; // Permite ejecutar consultas SQL con parámetros
        ResultSet resultSet = null;           // Contendrá los resultados devueltos por la consulta
        int duracionLlamada = 0;


        // Conexión y ejecución de la sentencia SQL
        // Introducimos los recursos que vamos a utilizar para la conexión dentro del parentesis del try
        // para que se cierren automáticamente y asi no tener que usar un finally(código más limpio)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement s = connection.createStatement()) {

            // Ejecuta la sentencia SQL de hacer una búsqueda en la tabla 'llamadas_emitidas'
            // De la columna NUMERO_LLAMADO
            // Ejecutamos la consulta 1
            String sql = "SELECT NUMERO_LLAMADO FROM llamadas_emitidas WHERE CODIGO_LLAMADA = 1000017";

            String numeroLlamado = resultSet.getString("NUMERO_LLAMADO");
            System.out.println(numeroLlamado);

            // Ejecutamos la consulta 2, con una consulta preparada(preparedStatement)


            //Con ResultSet almacenamos el resultado de la busqueda
            // --- 2️⃣ Preparamos la consulta SQL ---
            // La consulta tiene un parámetro (?) que se sustituirá con nuestro valor da la duraciónd de la llamada
            String consultaPreparada = "\"SELECT DURACION_LLAMADA FROM llamadas_emitidas WHERE \n" +
                    "CODIGO_LLAMADA = ?\" ";

            // Creamos el objeto PreparedStatement a partir de la conexión y la consulta SQL
            preparedStatement = connection.prepareStatement(sql);

            // --- 3️⃣ Sustituimos el parámetro de la consulta ---
            // El primer parámetro (?) se reemplaza por el valor de duración de la llamada
            preparedStatement.setInt(111, duracionLlamada);

            System.out.println("Ejecutando consulta para comprobar la duración de la llamada: " + duracionLlamada);

            // --- 4️⃣ Ejecutamos la consulta ---
            // executeQuery() ejecuta el SELECT y devuelve un ResultSet con los resultados
            resultSet = preparedStatement.executeQuery();

            System.out.println("Procesando resultados...");
            boolean encontrado = false; // Variable para saber si encontramos alguna llamada

            // --- 5️⃣ Recorremos el ResultSet para leer los datos obtenidos ---
            while (resultSet.next()) { // next() avanza a la siguiente fila (devuelve false si no hay más)
                encontrado = true;

                // Obtenemos los valores de las columnas de DURACION_LLAMADA
                int codigoAgente = resultSet.getInt("DURACION_LLAMADA");


                // Mostramos los datos de la llamada por consola
                System.out.println("Llamada encontrada:");
                System.out.println("Duración de la llamada: " + duracionLlamada);

                // --- 6️⃣ Si no se encontró ninguna llamada ---
                if (!encontrado) {
                    System.out.println("No se encontró ninguna llamada con duración de " + duracionLlamada);
                }
            }


            //Calculamos el coste total de la llamada
            //Paso 3: Ejecutamos la consulta
            String totalImporteLlamada = "SELECT *(SUM, IMPORTE_LLAMADA), FROM llamadas_emitidas";
            ResultSet resultado = s.executeQuery(sql);

            while (resultSet.next()) {
                totalImporteLlamada = String.valueOf(resultSet.getInt(Integer.parseInt("IMPORTE_LLAMADA")));
                System.out.println(" " + totalImporteLlamada + " ");


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
}
