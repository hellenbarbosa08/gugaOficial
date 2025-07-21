public class Cliente {
    private int id; // Adicionado
    private String nome;
    private String telefone;

    // Construtor para buscar do banco
    public Cliente(int id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
    }

    // Construtor para criar um novo cliente
    public Cliente(String nome, String telefone) {
        this.nome = (nome == null || nome.trim().isEmpty()) ? "Não informado" : nome;
        this.telefone = (telefone == null || telefone.trim().isEmpty()) ? "Não informado" : telefone;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
}