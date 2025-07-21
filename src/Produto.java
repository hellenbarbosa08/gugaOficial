public class Produto {
    private int id; // Adicionado
    private String nome;
    private double preco;

    // Construtor atualizado
    public Produto(int id, String nome, double preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
}