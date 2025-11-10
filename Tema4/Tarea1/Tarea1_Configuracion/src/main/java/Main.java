import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {

        System.out.println("Intentando conectar a la base de datos...");

        EntityManagerFactory emf = null;
        try {
            // Intenta crear el EntityManagerFactory.
            // "default" debe coincidir con el <persistence-unit name="default"> de tu persistence.xml
            emf = Persistence.createEntityManagerFactory("default");

            // Si llega aquí, la conexión fue exitosa.
            System.out.println("¡CONEXIÓN EXITOSA!");
            System.out.println("EntityManagerFactory creado: " + emf.getClass().getName());

        } catch (Exception e) {
            // Si falla, imprimirá el error exacto.
            System.err.println("ERROR AL CONECTAR:");
            e.printStackTrace();

        } finally {
            // Cierra el factory si se llegó a crear.
            if (emf != null) {
                emf.close();
                System.out.println("EntityManagerFactory cerrado.");
            }
        }
    }
}

