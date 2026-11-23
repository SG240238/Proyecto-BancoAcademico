import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = obtenerVariable(
            "BANCO_DB_URL",
            "jdbc:mysql://localhost:3306/banco_academico_db?useSSL=false&serverTimezone=UTC");
    private static final String USER = obtenerVariable("BANCO_DB_USER", "root");
    private static final String PASSWORD = obtenerVariable("BANCO_DB_PASSWORD", "");

    private Conexion() {
    }

    private static String obtenerVariable(String nombre, String valorPredeterminado) {
        String valor = System.getenv(nombre);
        return valor == null ? valorPredeterminado : valor;
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver de MySQL: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConexion() throws SQLException {
        Connection conexion = getConnection();
        if (conexion == null) {
            throw new SQLException(
                    "No se pudo establecer conexión con la base de datos.");
        }
        return conexion;
    }
}
