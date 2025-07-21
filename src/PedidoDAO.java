
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PedidoDAO {
    public void salvarPedido(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (cliente_id, forma_pagamento, tipo_consumo, valor_total) VALUES (?, ?, ?, ?)";
        String sqlItens = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia a transação

            long pedidoId;
            try (PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmtPedido.setInt(1, pedido.getCliente().getId());
                stmtPedido.setString(2, pedido.getFormaPagamento());
                stmtPedido.setString(3, pedido.getTipoConsumo());
                stmtPedido.setDouble(4, pedido.getValorTotal());
                stmtPedido.executeUpdate();

                ResultSet rs = stmtPedido.getGeneratedKeys();
                if (rs.next()) {
                    pedidoId = rs.getLong(1);
                } else {
                    throw new SQLException("Falha ao obter ID do pedido, nenhuma chave gerada.");
                }
            }

            try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                for (ItemPedido item : pedido.getItens()) {
                    stmtItens.setLong(1, pedidoId);
                    stmtItens.setInt(2, item.getProduto().getId());
                    stmtItens.setInt(3, item.getQuantidade());
                    stmtItens.setDouble(4, item.getSubtotal());
                    stmtItens.addBatch();
                }
                stmtItens.executeBatch();
            }

            conn.commit(); // Finaliza a transação com sucesso

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Desfaz tudo em caso de erro
            }
            throw new RuntimeException("Erro ao salvar o pedido no banco de dados.", e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}