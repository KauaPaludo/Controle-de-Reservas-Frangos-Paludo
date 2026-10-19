// view/TelaPedidos.java
package view;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import services.PedidoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;

public class TelaPedidos extends JFrame {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final PedidoService service;

    // Formulário
    private final JTextField txtNome = new JTextField(15);
    private final JTextField txtTelefone = new JTextField(15);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Frango", "Carne assada"});
    private final JTextField txtHorario = new JTextField(5);
    private final JSpinner spQuant = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JTextArea txtObs = new JTextArea(3, 15);

    // Filtros e tabela
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Pendentes", "Retirados", "Todos"});
    private List<Pedido> exibidos = new ArrayList<>();
    private final JComboBox<String> cbFiltro = new JComboBox<>(new String[]{"Todos", "Frango", "Carne assada"});
    private final JTextField txtBusca = new JTextField(12);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Horário", "Tipo", "Qtd", "Cliente", "Telefone", "Observação","Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JLabel lblTotais = new JLabel();

    public TelaPedidos(PedidoService service) {
        super("Pedidos - Retirada");
        this.service = service;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarFormulario(), BorderLayout.WEST);
        add(criarPainelLista(), BorderLayout.CENTER);

        atualizarTabela();
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Novo pedido"));

        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);

        adicionarLinha(painel, 0, "Nome:", txtNome);
        adicionarLinha(painel, 1, "Telefone:", txtTelefone);
        adicionarLinha(painel, 2, "Tipo:", cbTipo);
        adicionarLinha(painel, 3, "Horário (HH:mm):", txtHorario);
        adicionarLinha(painel, 4, "Quantidade:", spQuant);
        adicionarLinha(painel, 5, "Observação:", new JScrollPane(txtObs));

        JButton btnRegistrar = new JButton("Registrar pedido");
        btnRegistrar.addActionListener(e -> registrar());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(btnRegistrar, gbc);

        return painel;
    }

    private void adicionarLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = linha;
        painel.add(new JLabel(rotulo), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, gbc);
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Pedidos"));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Status:"));
        filtros.add(cbStatus);
        filtros.add(new JLabel("Tipo:"));
        filtros.add(cbFiltro);
        filtros.add(new JLabel("Nome:"));
        filtros.add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar");
        filtros.add(btnBuscar);
        filtros.add(btnLimpar);

        cbStatus.addActionListener(e -> atualizarTabela());
        cbFiltro.addActionListener(e -> atualizarTabela());
        btnBuscar.addActionListener(e -> atualizarTabela());
        txtBusca.addActionListener(e -> atualizarTabela());
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            cbFiltro.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            atualizarTabela();
        });

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Linhas de pedidos retirados ficam em cinza
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                                                           boolean selecionada, boolean foco, int linha, int coluna) {
                Component c = super.getTableCellRendererComponent(t, valor, selecionada, foco, linha, coluna);
                if (!selecionada) {
                    boolean retirado = linha < exibidos.size() && exibidos.get(linha).isRetirado();
                    c.setForeground(retirado ? Color.GRAY : t.getForeground());
                }
                return c;
            }
        });

        lblTotais.setFont(lblTotais.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnRetirado = new JButton("Marcar como retirado");
        JButton btnDesfazer = new JButton("Desfazer retirada");
        btnRetirado.addActionListener(e -> alterarRetirada(true));
        btnDesfazer.addActionListener(e -> alterarRetirada(false));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnDesfazer);
        botoes.add(btnRetirado);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(lblTotais, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);

        painel.add(filtros, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private void registrar() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || telefone.isEmpty()) {
            erro("Informe nome e telefone.");
            return;
        }

        LocalTime horario;
        try {
            horario = LocalTime.parse(txtHorario.getText().trim(), FMT);
        } catch (DateTimeParseException e) {
            erro("Horário inválido. Use o formato HH:mm, por exemplo 18:30.");
            return;
        }

        int quant = (Integer) spQuant.getValue();
        TipoPedido tipo = cbTipo.getSelectedIndex() == 0 ? TipoPedido.FRANGO : TipoPedido.CARNE;

        String obs = txtObs.getText().trim();
        if (obs.isEmpty()) {
            obs = "Sem observações";
        }

        try {
            service.registrar(new Cliente(nome, telefone), tipo, horario, quant, obs);
        } catch (RuntimeException e) {
            erro("Não foi possível salvar o pedido: " + e.getMessage());
            return;
        }

        limparFormulario();
        atualizarTabela();
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtTelefone.setText("");
        cbTipo.setSelectedIndex(0);
        txtHorario.setText("");
        spQuant.setValue(1);
        txtObs.setText("");
        txtNome.requestFocus();
    }

    private void atualizarTabela() {
        List<Pedido> lista;
        String busca = txtBusca.getText().trim();

        if (!busca.isEmpty()) {
            lista = service.buscarPorNome(busca);
        } else {
            lista = switch (cbFiltro.getSelectedIndex()) {
                case 1 -> service.listarPorTipo(TipoPedido.FRANGO);
                case 2 -> service.listarPorTipo(TipoPedido.CARNE);
                default -> service.listarTodos();
            };
        }

        int status = cbStatus.getSelectedIndex();   // 0 pendentes, 1 retirados, 2 todos
        exibidos = new ArrayList<>();
        for (Pedido p : lista) {
            if (status == 0 && p.isRetirado()) continue;
            if (status == 1 && !p.isRetirado()) continue;
            exibidos.add(p);
        }

        modelo.setRowCount(0);
        for (Pedido p : exibidos) {
            modelo.addRow(new Object[]{
                    p.getHorario().format(FMT),
                    p.getTipo() == TipoPedido.FRANGO ? "Frango" : "Carne assada",
                    String.valueOf(p.getQuant()),   // texto, para o renderer cinza valer na coluna
                    p.getCliente().getName(),
                    p.getCliente().getNumber(),
                    p.getAob(),
                    p.isRetirado() ? "Retirado" : "Pendente"
            });
        }

        lblTotais.setText("Falta preparar  |  Frango: " + service.totalPorTipo(TipoPedido.FRANGO)
                + "  |  Carne assada: " + service.totalPorTipo(TipoPedido.CARNE));
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
    private void alterarRetirada(boolean retirado) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            erro("Selecione um pedido na tabela.");
            return;
        }
        Pedido p = exibidos.get(linha);
        try {
            service.marcarRetirado(p.getId(), retirado);
        } catch (RuntimeException e) {
            erro("Não foi possível atualizar o pedido: " + e.getMessage());
            return;
        }
        atualizarTabela();
    }
}