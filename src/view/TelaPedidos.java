// view/TelaPedidos.java
package view;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import services.PedidoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import util.Entrada;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;

public class TelaPedidos extends JFrame {

    private final PedidoService service;

    // Formulário
    private final JTextField txtNome = new JTextField(15);
    private final JTextField txtTelefone = new JTextField(15);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Frango", "Carne assada"});
    private final JTextField txtData = new JTextField(LocalDate.now().format(Entrada.DATA), 8);
    private final JTextField txtHorario = new JTextField(5);
    private final JLabel lblQuant = new JLabel("Quantidade (un):");
    private final JTextField txtQuant = new JTextField("1", 5);
    private final JTextArea txtObs = new JTextArea(3, 15);

    // Filtros e tabela
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Pendentes", "Retirados", "Todos"});
    private List<Pedido> exibidos = new ArrayList<>();
    private final JComboBox<String> cbFiltro = new JComboBox<>(new String[]{"Todos", "Frango", "Carne assada"});
    private final JTextField txtBusca = new JTextField(12);
    private final JTextField txtDia = new JTextField(LocalDate.now().format(Entrada.DATA), 8);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Data", "Horário", "Tipo", "Qtd / Peso", "Cliente", "Telefone", "Observação", "Status"}, 0) {
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
        setSize(1150, 550);
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

        cbTipo.addActionListener(e -> ajustarCampoQuantidade());

        adicionarLinha(painel, 0, new JLabel("Nome:"), txtNome);
        adicionarLinha(painel, 1, new JLabel("Telefone (opcional):"), txtTelefone);
        adicionarLinha(painel, 2, new JLabel("Tipo:"), cbTipo);
        adicionarLinha(painel, 3, new JLabel("Data (dd/MM/aaaa):"), txtData);
        adicionarLinha(painel, 4, new JLabel("Horário (HH:mm):"), txtHorario);
        adicionarLinha(painel, 5, lblQuant, txtQuant);
        adicionarLinha(painel, 6, new JLabel("Observação:"), new JScrollPane(txtObs));

        JButton btnRegistrar = new JButton("Registrar pedido");
        btnRegistrar.addActionListener(e -> registrar());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(btnRegistrar, gbc);

        return painel;
    }

    /** Frango é vendido por unidade; carne assada, por peso (kg). */
    private void ajustarCampoQuantidade() {
        boolean carne = cbTipo.getSelectedIndex() == 1;
        lblQuant.setText(carne ? "Peso (kg):" : "Quantidade (un):");
        txtQuant.setText(carne ? "" : "1");
        txtQuant.requestFocus();
    }

    private void adicionarLinha(JPanel painel, int linha, JComponent rotulo, JComponent campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = linha;
        painel.add(rotulo, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, gbc);
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Pedidos"));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Dia (vazio = todos):"));
        filtros.add(txtDia);
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
        txtDia.addActionListener(e -> atualizarTabela());
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            txtDia.setText(LocalDate.now().format(Entrada.DATA));
            cbFiltro.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            atualizarTabela();
        });

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(80);

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
        if (nome.isEmpty()) {
            erro("Informe o nome do cliente.");
            return;
        }

        TipoPedido tipo = cbTipo.getSelectedIndex() == 0 ? TipoPedido.FRANGO : TipoPedido.CARNE;
        String telefone;
        LocalDate data;
        LocalTime horario;
        double quantidade;
        try {
            telefone = Entrada.telefone(txtTelefone.getText());
            data = Entrada.data(txtData.getText());
            horario = Entrada.hora(txtHorario.getText());
            quantidade = tipo == TipoPedido.CARNE
                    ? Entrada.peso(txtQuant.getText())
                    : Entrada.unidades(txtQuant.getText());
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
            return;
        }

        String obs = txtObs.getText().trim();
        if (obs.isEmpty()) {
            obs = "Sem observações";
        }

        try {
            service.registrar(new Cliente(nome, telefone), tipo, data, horario, quantidade, obs);
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
        txtData.setText(LocalDate.now().format(Entrada.DATA));
        txtHorario.setText("");
        txtQuant.setText("1");
        lblQuant.setText("Quantidade (un):");
        txtObs.setText("");
        txtNome.requestFocus();
    }

    private void atualizarTabela() {
        LocalDate dia = null;
        String textoDia = txtDia.getText().trim();
        if (!textoDia.isEmpty()) {
            try {
                dia = Entrada.data(textoDia);
            } catch (IllegalArgumentException e) {
                erro(e.getMessage());
                return;
            }
        }

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
            if (dia != null && !p.getData().equals(dia)) continue;
            if (status == 0 && p.isRetirado()) continue;
            if (status == 1 && !p.isRetirado()) continue;
            exibidos.add(p);
        }

        modelo.setRowCount(0);
        for (Pedido p : exibidos) {
            String telefone = p.getCliente().getNumber();
            modelo.addRow(new Object[]{
                    p.getData().format(Entrada.DATA),
                    p.getHorario().format(Entrada.HORA),
                    p.getTipo() == TipoPedido.FRANGO ? "Frango" : "Carne assada",
                    p.getQuantidadeFormatada(),
                    p.getCliente().getName(),
                    telefone.isEmpty() ? "-" : telefone,
                    p.getObservacao(),
                    p.isRetirado() ? "Retirado" : "Pendente"
            });
        }

        String periodo = dia == null ? "todos os dias" : dia.format(Entrada.DATA);
        lblTotais.setText("Falta preparar (" + periodo + ")  |  Frango: "
                + Pedido.formatarQuantidade(TipoPedido.FRANGO, service.totalPorTipo(TipoPedido.FRANGO, dia))
                + "  |  Carne assada: "
                + Pedido.formatarQuantidade(TipoPedido.CARNE, service.totalPorTipo(TipoPedido.CARNE, dia)));
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