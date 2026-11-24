package tarefas.tela;

import tarefas.modelo.*;
import tarefas.servico.AgendaTarefas;
import tarefas.util.Datas;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class JanelaTarefas extends JFrame {
    private final AgendaTarefas agenda = new AgendaTarefas();
    private final JTextField busca = new JTextField(22);
    private final JComboBox<String> filtro = new JComboBox<>(new String[]{"Todas", "Pendente", "Atrasada", "Concluída"});
    private final JLabel resumo = new JLabel();
    private final JLabel contagem = new JLabel();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Código", "Título", "Responsável", "Prioridade", "Prazo", "Tipo", "Progresso", "Situação"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public JanelaTarefas() {
        super("Organiza — Gerenciador de Tarefas");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1080, 620);
        setMinimumSize(new Dimension(960, 520));
        setLocationRelativeTo(null);
        JPanel conteudo = new JPanel(new BorderLayout(12, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setContentPane(conteudo);

        JPanel cabecalho = new JPanel(new GridLayout(3, 1, 0, 8));
        JLabel titulo = new JLabel("Organiza | Minhas tarefas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        cabecalho.add(titulo);
        cabecalho.add(new JLabel("Tarefas simples e checklists • Os dados são apagados ao fechar o programa."));
        resumo.setFont(resumo.getFont().deriveFont(Font.BOLD, 15f));
        cabecalho.add(resumo);
        conteudo.add(cabecalho, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        JPanel pesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pesquisa.add(new JLabel("Título ou responsável:")); pesquisa.add(busca);
        pesquisa.add(new JLabel("Situação:")); pesquisa.add(filtro);
        JButton consultar = new JButton("Consultar / atualizar");
        consultar.addActionListener(e -> atualizar());
        busca.addActionListener(e -> atualizar());
        filtro.addActionListener(e -> atualizar());
        pesquisa.add(consultar);
        JButton limpar = new JButton("Limpar filtros");
        limpar.addActionListener(e -> { busca.setText(""); filtro.setSelectedIndex(0); atualizar(); });
        pesquisa.add(limpar);
        centro.add(pesquisa, BorderLayout.NORTH);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(28);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(140);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        centro.add(contagem, BorderLayout.SOUTH);
        conteudo.add(centro, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton novo = new JButton("Nova tarefa");
        JButton editar = new JButton("Editar dados");
        JButton progresso = new JButton("Acompanhar tarefa");
        JButton excluir = new JButton("Excluir");
        JButton sair = new JButton("Sair");
        novo.addActionListener(e -> new FormularioTarefa(this, agenda, null).setVisible(true));
        editar.addActionListener(e -> {
            Tarefa tarefa = selecionada();
            if (tarefa != null) new FormularioTarefa(this, agenda, tarefa).setVisible(true);
        });
        progresso.addActionListener(e -> {
            Tarefa tarefa = selecionada();
            if (tarefa != null) new AcompanhamentoTarefa(this, tarefa).setVisible(true);
        });
        excluir.addActionListener(e -> excluir());
        sair.addActionListener(e -> sair());
        acoes.add(novo); acoes.add(editar); acoes.add(progresso); acoes.add(excluir); acoes.add(sair);
        conteudo.add(acoes, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { sair(); }
        });
        atualizar();
    }

    public void atualizar() {
        LocalDate hoje = LocalDate.now();
        modelo.setRowCount(0);
        for (Tarefa tarefa : agenda.consultar(busca.getText(), (String) filtro.getSelectedItem(), hoje)) {
            modelo.addRow(new Object[]{tarefa.getCodigo(), tarefa.getTitulo(), tarefa.getResponsavel(),
                tarefa.getPrioridade(), Datas.formatar(tarefa.getPrazo()), tarefa.getTipo(),
                tarefa.calcularProgresso() + "%", tarefa.getSituacao(hoje)});
        }
        resumo.setText("Total: " + agenda.getTotal() + "     |     Concluídas: " + agenda.contarConcluidas()
                + "     |     Em aberto: " + (agenda.getTotal() - agenda.contarConcluidas())
                + "     |     Atrasadas: " + agenda.contarAtrasadas(hoje));
        contagem.setText(modelo.getRowCount() + " tarefa(s) exibida(s). Resumo geral • Atualizado em " + Datas.formatar(hoje));
    }

    public void mostrarTodas() {
        busca.setText(""); filtro.setSelectedIndex(0); atualizar();
    }

    private Tarefa selecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.", "Seleção necessária", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return agenda.localizar((Integer) modelo.getValueAt(linha, 0));
    }
    private void excluir() {
        Tarefa tarefa = selecionada();
        if (tarefa == null) return;
        int resposta = JOptionPane.showConfirmDialog(this, "Excluir a tarefa de código " + tarefa.getCodigo() + " e suas etapas?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            agenda.excluir(tarefa.getCodigo()); atualizar();
            JOptionPane.showMessageDialog(this, "Tarefa excluída.");
        }
    }
    private void sair() {
        if (JOptionPane.showConfirmDialog(this, "Os dados serão apagados ao sair. Deseja continuar?", "Encerrar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) dispose();
    }
}
