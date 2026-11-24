package tarefas.servico;

import tarefas.modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Cadastro e consulta em memória, independente dos componentes Swing. */
public class AgendaTarefas {
    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoCodigo = 1;

    public void cadastrarSimples(String titulo, String responsavel, String prioridade, LocalDate prazo) {
        tarefas.add(new TarefaSimples(proximoCodigo, titulo, responsavel, prioridade, prazo));
        proximoCodigo++;
    }
    public void cadastrarChecklist(String titulo, String responsavel, String prioridade,
                                  LocalDate prazo, List<String> etapas) {
        tarefas.add(new TarefaChecklist(proximoCodigo, titulo, responsavel, prioridade, prazo, etapas));
        proximoCodigo++;
    }
    public Tarefa localizar(int codigo) {
        for (Tarefa tarefa : tarefas) if (tarefa.getCodigo() == codigo) return tarefa;
        throw new IllegalArgumentException("Tarefa não encontrada.");
    }
    public void alterar(int codigo, String titulo, String responsavel, String prioridade, LocalDate prazo) {
        localizar(codigo).atualizarDados(titulo, responsavel, prioridade, prazo);
    }
    public void excluir(int codigo) { tarefas.remove(localizar(codigo)); }

    public List<Tarefa> consultar(String busca, String situacao, LocalDate hoje) {
        List<Tarefa> resultado = new ArrayList<>();
        String termo = busca == null ? "" : busca.trim().toLowerCase(Locale.ROOT);
        for (Tarefa tarefa : tarefas) {
            String texto = (tarefa.getTitulo() + " " + tarefa.getResponsavel()).toLowerCase(Locale.ROOT);
            if (texto.contains(termo) && ("Todas".equals(situacao) || tarefa.getSituacao(hoje).equals(situacao)))
                resultado.add(tarefa);
        }
        return resultado;
    }
    public int contarConcluidas() {
        int total = 0;
        // Polimorfismo: cada objeto decide se sua própria regra de conclusão foi cumprida.
        for (Tarefa tarefa : tarefas) if (tarefa.estaConcluida()) total++;
        return total;
    }
    public int contarAtrasadas(LocalDate hoje) {
        int total = 0;
        for (Tarefa tarefa : tarefas) if (tarefa.estaAtrasada(hoje)) total++;
        return total;
    }
    public int getTotal() { return tarefas.size(); }
}
