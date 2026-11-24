package tarefas;

import javax.swing.SwingUtilities;
import tarefas.tela.JanelaTarefas;

public class Aplicacao {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JanelaTarefas().setVisible(true));
    }
}
