import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pedido {
    private Cliente cliente;
    private List<ItemPedido> itens;
    private String formaPagamento;
    private String tipoConsumo;

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.itens = new ArrayList<>();
    }

    private Optional<ItemPedido> findItemByProduto(Produto produto) {
        return this.itens.stream()
                .filter(item -> item.getProduto().getId() == produto.getId())
                .findFirst();
    }

    public void adicionarOuIncrementarItem(Produto produto, int quantidade) {
        Optional<ItemPedido> itemExistente = findItemByProduto(produto);
        if (itemExistente.isPresent()) {
            itemExistente.get().incrementarQuantidade(quantidade);
        } else {
            this.itens.add(new ItemPedido(produto, quantidade));
        }
    }

    public void removerOuDecrementarItem(Produto produto, int quantidade) {
        Optional<ItemPedido> itemExistente = findItemByProduto(produto);
        if (itemExistente.isPresent()) {
            ItemPedido item = itemExistente.get();
            item.decrementarQuantidade(quantidade);
            if (item.getQuantidade() <= 0) {
                this.itens.remove(item);
            }
        }
    }

    public void removerItemPorCompleto(Produto produto) {
        findItemByProduto(produto).ifPresent(item -> this.itens.remove(item));
    }

    public double getValorTotal() {
        return this.itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    public String gerarResumo() {
        StringBuilder resumo = new StringBuilder("===== RESUMO DO PEDIDO =====\n\n");
        resumo.append("Cliente: ").append(cliente.getNome()).append("\n");
        resumo.append("Telefone: ").append(cliente.getTelefone()).append("\n\n");
        resumo.append("Itens:\n");
        for (ItemPedido item : itens) {
            resumo.append(String.format("- %s (%d un) - R$ %.2f\n",
                    item.getProduto().getNome(), item.getQuantidade(), item.getSubtotal()));
        }
        resumo.append("\n----------------------------------\n");
        resumo.append(String.format("TOTAL: R$ %.2f\n", getValorTotal()));
        resumo.append("Forma de Pagamento: ").append(formaPagamento).append("\n");
        resumo.append("Consumo: ").append(tipoConsumo).append("\n");
        return resumo.toString();
    }

    // Getters e Setters
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<ItemPedido> getItens() { return itens; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public String getTipoConsumo() { return tipoConsumo; }
    public void setTipoConsumo(String tipoConsumo) { this.tipoConsumo = tipoConsumo; }
}