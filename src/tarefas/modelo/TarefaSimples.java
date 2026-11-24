package tarefas.modelo;

import java.time.LocalDate;

public class TarefaSimples extends Tarefa {
    private boolean concluida;

    public TarefaSimples(int codigo, String titulo, String responsavel, String prioridade, LocalDate prazo) {
        super(codigo, titulo, responsavel, prioridade, prazo);
    }
    public void concluir() { concluida = true; }
    public void reabrir() { concluida = false; }
    @Override
    public String getTipo() { return "Simples"; }
    @Override
    public int calcularProgresso() { return concluida ? 100 : 0; }
    @Override
    public boolean estaConcluida() { return concluida; }
}
