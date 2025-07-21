import java.sql.*;

public class ClienteDAO {

    // Busca um cliente pelo telefone. Se não achar, cria um novo.
    public Cliente buscarOuCriarCliente(Cliente cliente) {
        // Tenta encontrar o cliente pelo telefone
        String sqlSelect = "SELECT id, nome, telefone FROM clientes WHERE telefone = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setString(1, cliente.getTelefone());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Se encontrou, retorna o cliente existente com o ID do banco
                return new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente.", e);
        }

        // Se não encontrou, insere o novo cliente no banco
        String sqlInsert = "INSERT INTO clientes (nome, telefone) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                cliente.setId(generatedKeys.getInt(1)); // Pega o ID gerado pelo banco e atualiza o objeto
            }
            return cliente;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar novo cliente.", e);
        }
    }
}