import javax.swing.SwingUtilities;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Tenta estabelecer uma conexão apenas para verificar se o banco está acessível
        try (Connection conn = ConnectionFactory.getConnection()) {
            System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
            // Inicia a interface gráfica
            SwingUtilities.invokeLater(GugaLanchesGUI::new);
        } catch (Exception e) {
            System.err.println("Falha na conexão com o banco de dados! Verifique as credenciais e o serviço.");
            e.printStackTrace();
        }
    }
}