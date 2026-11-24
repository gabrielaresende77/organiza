package tarefas.tela;

import tarefas.modelo.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/** Mantém as mudanças locais até o usuário clicar em Salvar. */
public class AcompanhamentoTarefa extends JDialog {
    public AcompanhamentoTarefa(JanelaTarefas janela, Tarefa tarefa) {
        super(janela, "Acompanhar tarefa #" + tarefa.getCodigo(), true);
        JPanel conteudo = new JPanel(new BorderLayout(10, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel titulo = new JLabel(tarefa.getTitulo());
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        conteudo.add(titulo, BorderLayout.NORTH);
        JPanel opcoes = new JPanel(); opcoes.setLayout(new BoxLayout(opcoes, BoxLayout.Y_AXIS));
        List<JCheckBox> caixas = new ArrayList<>();
        if (tarefa instanceof TarefaSimples) {
            JCheckBox concluida = new JCheckBox("Tarefa concluída", tarefa.estaConcluida());
            caixas.add(concluida); opcoes.add(concluida);
        } else if (tarefa instanceof TarefaChecklist) {
            TarefaChecklist checklist = (TarefaChecklist) tarefa;
            for (int i = 0; i < checklist.getQuantidadeEtapas(); i++) {
                JCheckBox caixa = new JCheckBox(checklist.getEtapa(i), checklist.isEtapaRealizada(i));
                caixas.add(caixa); opcoes.add(caixa);
            }
        }
        conteudo.add(new JScrollPane(opcoes), BorderLayout.CENTER);
        conteudo.add(new JLabel("Marque ou desmarque os itens e salve. Cancelar descarta as mudanças."), BorderLayout.SOUTH);
        add(conteudo, BorderLayout.CENTER);
        JButton salvar = new JButton("Salvar progresso");
        JButton cancelar = new JButton("Cancelar");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(salvar); botoes.add(cancelar); add(botoes, BorderLayout.SOUTH);
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> {
            if (tarefa instanceof TarefaSimples) {
                TarefaSimples simples = (TarefaSimples) tarefa;
                if (caixas.get(0).isSelected()) simples.concluir(); else simples.reabrir();
            } else if (tarefa instanceof TarefaChecklist) {
                TarefaChecklist checklist = (TarefaChecklist) tarefa;
                for (int i = 0; i < caixas.size(); i++) checklist.marcarEtapa(i, caixas.get(i).isSelected());
            }
            janela.atualizar(); dispose();
            JOptionPane.showMessageDialog(janela, "Progresso atualizado.");
        });
        setSize(640, 380); setMinimumSize(new Dimension(580, 300)); setLocationRelativeTo(janela);
    }
}
