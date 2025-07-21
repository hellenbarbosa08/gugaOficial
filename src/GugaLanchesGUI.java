import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GugaLanchesGUI extends JFrame {

    // --- Componentes da GUI ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel paineisPrincipais = new JPanel(cardLayout);
    private JPanel painelCarrinhoItens;
    private JButton btnIrParaCarrinho;
    private JLabel lblTotalCarrinho;
    private JComboBox<String> comboFormaPagamento, comboConsumo;
    private JTextArea areaResumo;

    // --- Modelos de Dados e DAOs ---
    private List<Produto> cardapio = new ArrayList<>();
    private Pedido pedidoAtual;
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public GugaLanchesGUI() {
        setTitle("Guga Lanches");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Carrega o cardápio do banco de dados
        carregarCardapioDoBanco();

        // Inicia um pedido vazio para o "Convidado"
        this.pedidoAtual = new Pedido(new Cliente("Convidado", ""));

        // Cria as "telas" da aplicação
        JPanel painelMenu = criarPainelMenu();
        JPanel painelCarrinho = criarPainelCarrinho();
        JPanel painelResumo = criarPainelResumo();

        paineisPrincipais.add(painelMenu, "MENU");
        paineisPrincipais.add(painelCarrinho, "CARRINHO");
        paineisPrincipais.add(painelResumo, "RESUMO");

        add(paineisPrincipais);

        cardLayout.show(paineisPrincipais, "MENU");
        setVisible(true);
    }

    // Carrega o cardápio usando o DAO
    private void carregarCardapioDoBanco() {
        try {
            this.cardapio = produtoDAO.listarProdutos();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o cardápio: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Cria o painel do menu principal
    private JPanel criarPainelMenu() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(new JLabel("<html><h1>Cardápio Guga Lanches</h1></html>", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel painelProdutos = new JPanel(new GridLayout(0, 2, 10, 10));
        for (Produto produto : cardapio) {
            painelProdutos.add(criarPainelDeUmProduto(produto));
        }
        painel.add(new JScrollPane(painelProdutos), BorderLayout.CENTER);

        btnIrParaCarrinho = new JButton("Ir para o Carrinho");
        btnIrParaCarrinho.setFont(new Font("Arial", Font.BOLD, 16));
        btnIrParaCarrinho.setVisible(false);
        btnIrParaCarrinho.addActionListener(e -> cardLayout.show(paineisPrincipais, "CARRINHO"));
        painel.add(btnIrParaCarrinho, BorderLayout.SOUTH);

        return painel;
    }

    // Cria o painel para um único produto no menu
    private JPanel criarPainelDeUmProduto(Produto produto) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createEtchedBorder());

        JLabel lblNomePreco = new JLabel(String.format("%s - R$ %.2f", produto.getNome(), produto.getPreco()));
        lblNomePreco.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnAdicionar = new JButton("+");
        btnAdicionar.addActionListener(e -> {
            adicionarAoCarrinho(produto);
            if (!btnIrParaCarrinho.isVisible()) {
                btnIrParaCarrinho.setVisible(true);
            }
        });

        painel.add(btnAdicionar);
        painel.add(lblNomePreco);
        return painel;
    }

    // Adiciona um produto ao pedido atual
    private void adicionarAoCarrinho(Produto produto) {
        pedidoAtual.adicionarOuIncrementarItem(produto, 1);
        atualizarVisualizacaoCarrinho();
    }

    // Cria o painel do carrinho de compras
    private JPanel criarPainelCarrinho() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(new JLabel("<html><h1>Seu Carrinho</h1></html>", SwingConstants.CENTER), BorderLayout.NORTH);

        painelCarrinhoItens = new JPanel();
        painelCarrinhoItens.setLayout(new BoxLayout(painelCarrinhoItens, BoxLayout.Y_AXIS));
        painel.add(new JScrollPane(painelCarrinhoItens), BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout(10, 10));
        JPanel painelOpcoes = new JPanel(new GridLayout(0, 2, 10, 10));

        comboFormaPagamento = new JComboBox<>(new String[]{"Dinheiro", "Cartão", "Pix"});
        comboConsumo = new JComboBox<>(new String[]{"Consumir no Local", "Retirada"});

        painelOpcoes.add(new JLabel("Forma de Pagamento:"));
        painelOpcoes.add(comboFormaPagamento);
        painelOpcoes.add(new JLabel("Tipo de Consumo:"));
        painelOpcoes.add(comboConsumo);

        lblTotalCarrinho = new JLabel("Total: R$ 0.00");
        lblTotalCarrinho.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnConfirmarPedido = new JButton("Confirmar Pedido");
        btnConfirmarPedido.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirmarPedido.addActionListener(e -> {
            try {
                abrirDialogoCliente();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }); // Este método agora salva no banco

        JButton btnVoltarMenu = new JButton("Voltar ao Cardápio");
        btnVoltarMenu.addActionListener(e -> cardLayout.show(paineisPrincipais, "MENU"));

        painelSul.add(painelOpcoes, BorderLayout.NORTH);
        painelSul.add(lblTotalCarrinho, BorderLayout.CENTER);

        JPanel painelBotoesCarrinho = new JPanel(new FlowLayout());
        painelBotoesCarrinho.add(btnVoltarMenu);
        painelBotoesCarrinho.add(btnConfirmarPedido);

        painelSul.add(painelBotoesCarrinho, BorderLayout.SOUTH);
        painel.add(painelSul, BorderLayout.SOUTH);

        return painel;
    }

    // Atualiza a exibição dos itens no painel do carrinho
    private void atualizarVisualizacaoCarrinho() {
        painelCarrinhoItens.removeAll();

        if (pedidoAtual.getItens().isEmpty()) {
            painelCarrinhoItens.add(new JLabel("Seu carrinho está vazio."));
        } else {
            // CORRIGIDO para usar ItemPedido
            for (ItemPedido item : pedidoAtual.getItens()) {
                painelCarrinhoItens.add(criarPainelItemCarrinho(item));
            }
        }

        lblTotalCarrinho.setText(String.format("Total: R$ %.2f", pedidoAtual.getValorTotal()));
        painelCarrinhoItens.revalidate();
        painelCarrinhoItens.repaint();
    }

    // Cria o painel para um único item no carrinho
    private JPanel criarPainelItemCarrinho(ItemPedido item) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel infoItem = new JLabel(String.format("<html><b>%s</b> (%d un) - Subtotal: R$ %.2f</html>",
                item.getProduto().getNome(), item.getQuantidade(), item.getSubtotal()));
        infoItem.setMinimumSize(new Dimension(300, 30));
        infoItem.setPreferredSize(new Dimension(300, 30));

        JButton btnMenos = new JButton("-");
        btnMenos.addActionListener(e -> {
            pedidoAtual.removerOuDecrementarItem(item.getProduto(), 1);
            atualizarVisualizacaoCarrinho();
        });

        JButton btnMais = new JButton("+");
        btnMais.addActionListener(e -> {
            pedidoAtual.adicionarOuIncrementarItem(item.getProduto(), 1);
            atualizarVisualizacaoCarrinho();
        });

        JButton btnExcluir = new JButton("X");
        btnExcluir.setForeground(Color.RED);
        btnExcluir.addActionListener(e -> {
            pedidoAtual.removerItemPorCompleto(item.getProduto());
            atualizarVisualizacaoCarrinho();
        });

        painel.add(infoItem);
        painel.add(btnMenos);
        painel.add(btnMais);
        painel.add(btnExcluir);
        return painel;
    }
    
    // Lida com a finalização e salvamento no banco
    private void abrirDialogoCliente() throws SQLException {
        if (pedidoAtual.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seu carrinho está vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nomeField = new JTextField();
        JTextField telefoneField = new JTextField();
        final JComponent[] inputs = new JComponent[]{
                new JLabel("Nome:"), nomeField,
                new JLabel("Telefone:"), telefoneField
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Informe seus dados para finalizar", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // INÍCIO DA LÓGICA DE BANCO DE DADOS
            try {
                // 1. Busca ou cria o cliente no banco
                ClienteDAO clienteDAO = new ClienteDAO();
                Cliente cliente = new Cliente(nomeField.getText(), telefoneField.getText());
                Cliente clienteSalvo = clienteDAO.buscarOuCriarCliente(cliente);

                // 2. Atualiza o pedido com os dados finais
                pedidoAtual.setCliente(clienteSalvo);
                pedidoAtual.setFormaPagamento((String) comboFormaPagamento.getSelectedItem());
                pedidoAtual.setTipoConsumo((String) comboConsumo.getSelectedItem());

                // 3. Salva o pedido completo no banco de dados
                PedidoDAO pedidoDAO = new PedidoDAO();
                pedidoDAO.salvarPedido(pedidoAtual);

                // 4. Se tudo deu certo, mostra o resumo
                areaResumo.setText(pedidoAtual.gerarResumo() + "\n\n===== Status =====\nSeu pedido foi recebido e já estamos preparando!");
                cardLayout.show(paineisPrincipais, "RESUMO");

            } catch (RuntimeException ex) {
                // Mostra uma mensagem de erro se a comunicação com o banco falhar
                JOptionPane.showMessageDialog(this, "Erro ao finalizar pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Ajuda a ver o erro detalhado no console
            }
        }
    }

    // Cria o painel de resumo final
    private JPanel criarPainelResumo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaResumo = new JTextArea("Aguardando finalização do pedido...");
        areaResumo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaResumo.setEditable(false);
        painel.add(new JScrollPane(areaResumo), BorderLayout.CENTER);

        JButton btnNovoPedido = new JButton("Fazer Novo Pedido");
        btnNovoPedido.addActionListener(e -> {
            // Reseta o pedido atual e volta para o menu
            this.pedidoAtual = new Pedido(new Cliente("Convidado", ""));
            atualizarVisualizacaoCarrinho();
            btnIrParaCarrinho.setVisible(false);
            cardLayout.show(paineisPrincipais, "MENU");
        });
        painel.add(btnNovoPedido, BorderLayout.SOUTH);

        return painel;
    }
}