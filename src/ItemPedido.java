// Arquivo: ItemPedido.java
public class ItemPedido {
    private Produto produto;
    private int quantidade;

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public void incrementarQuantidade(int valor) {
        this.quantidade += valor;
    }

    public void decrementarQuantidade(int valor) {
        this.quantidade -= valor;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getSubtotal() { return produto.getPreco() * quantidade; }
}