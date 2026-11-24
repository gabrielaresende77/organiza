package tarefas.tela;

import tarefas.modelo.Tarefa;
import tarefas.servico.AgendaTarefas;
import tarefas.util.Datas;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/** Cadastro e edição dos dados comuns. O tipo e as etapas são definidos no cadastro. */
public class FormularioTarefa extends JDialog {
    public FormularioTarefa(JanelaTarefas janela, AgendaTarefas agenda, Tarefa tarefa) {
        super(janela, tarefa == null ? "Nova tarefa" : "Editar dados da tarefa", true);
        JTextField titulo = new JTextField(26);
        JTextField responsavel = new JTextField(26);
        JComboBox<String> prioridade = new JComboBox<>(new String[]{"Baixa", "Média", "Alta"});
        prioridade.setSelectedItem("Média");
        JTextField prazo = new JTextField(Datas.formatar(LocalDate.now()));
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Simples", "Checklist"});
        JTextArea etapas = new JTextArea(6, 32);
        etapas.setLineWrap(true); etapas.setWrapStyleWord(true);
        etapas.setEnabled(false);
        tipo.addActionListener(e -> etapas.setEnabled("Checklist".equals(tipo.getSelectedItem())));
        JPanel campos = new JPanel(new GridLayout(5, 2, 10, 10));
        campos.add(new JLabel("Título:")); campos.add(titulo);
        campos.add(new JLabel("Responsável:")); campos.add(responsavel);
        campos.add(new JLabel("Prioridade:")); campos.add(prioridade);
        campos.add(new JLabel("Prazo (dd/mm/aaaa):")); campos.add(prazo);
        campos.add(new JLabel("Tipo:")); campos.add(tipo);
        JPanel centro = new JPanel(new BorderLayout(10, 12));
        centro.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        centro.add(campos, BorderLayout.NORTH);
        if (tarefa == null) {
            JPanel lista = new JPanel(new BorderLayout(6, 6));
            lista.add(new JLabel("Checklist: escreva uma etapa por linha (obrigatório nesse tipo)."), BorderLayout.NORTH);
            lista.add(new JScrollPane(etapas), BorderLayout.CENTER);
            centro.add(lista, BorderLayout.CENTER);
        } else {
            titulo.setText(tarefa.getTitulo()); responsavel.setText(tarefa.getResponsavel());
            prioridade.setSelectedItem(tarefa.getPrioridade()); prazo.setText(Datas.formatar(tarefa.getPrazo()));
            tipo.setSelectedItem(tarefa.getTipo()); tipo.setEnabled(false);
            centro.add(new JLabel("Tipo e etapas são fixos. Use Acompanhar tarefa para atualizar a conclusão."), BorderLayout.SOUTH);
        }
        add(centro, BorderLayout.CENTER);
        JButton salvar = new JButton("Salvar");
        JButton cancelar = new JButton("Cancelar");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(salvar); botoes.add(cancelar); add(botoes, BorderLayout.SOUTH);
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> {
            try {
                LocalDate data = Datas.ler(prazo.getText());
                String nivel = (String) prioridade.getSelectedItem();
                if (tarefa != null) {
                    agenda.alterar(tarefa.getCodigo(), titulo.getText(), responsavel.getText(), nivel, data);
                } else if ("Simples".equals(tipo.getSelectedItem())) {
                    agenda.cadastrarSimples(titulo.getText(), responsavel.getText(), nivel, data);
                } else {
                    List<String> itens = new ArrayList<>();
                    for (String linha : etapas.getText().split("\\R")) {
                        if (!linha.trim().isEmpty()) itens.add(linha.trim());
                    }
                    agenda.cadastrarChecklist(titulo.getText(), responsavel.getText(), nivel, data, itens);
                }
                janela.mostrarTodas(); dispose();
                JOptionPane.showMessageDialog(janela, "Tarefa salva com sucesso.");
            } catch (IllegalArgumentException erro) {
                JOptionPane.showMessageDialog(this, erro.getMessage(), "Confira os dados", JOptionPane.WARNING_MESSAGE);
            }
        });
        getRootPane().setDefaultButton(salvar);
        pack(); setResizable(false); setLocationRelativeTo(janela);
    }
}
