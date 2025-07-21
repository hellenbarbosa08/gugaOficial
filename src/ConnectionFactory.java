
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // ATENÇÃO: Verifique se a URL, usuário e senha estão corretos para o seu computador
    private static final String URL = "jdbc:postgresql://localhost:5432/guga";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456"; // <-- TROQUE PELA SUA SENHA

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + e.getMessage(), e);
        }
    }
}