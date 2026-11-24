package tarefas.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** A tarefa termina automaticamente quando todas as etapas são marcadas. */
public class TarefaChecklist extends Tarefa {
    private final List<String> etapas = new ArrayList<>();
    private final List<Boolean> realizadas = new ArrayList<>();

    public TarefaChecklist(int codigo, String titulo, String responsavel, String prioridade,
                           LocalDate prazo, List<String> etapas) {
        super(codigo, titulo, responsavel, prioridade, prazo);
        if (etapas == null || etapas.isEmpty())
            throw new IllegalArgumentException("Informe pelo menos uma etapa para o checklist.");
        for (String etapa : etapas) {
            if (etapa == null || etapa.trim().isEmpty())
                throw new IllegalArgumentException("As etapas não podem ficar vazias.");
            this.etapas.add(etapa.trim());
            realizadas.add(false);
        }
    }

    public int getQuantidadeEtapas() { return etapas.size(); }
    public String getEtapa(int indice) { validarIndice(indice); return etapas.get(indice); }
    public boolean isEtapaRealizada(int indice) { validarIndice(indice); return realizadas.get(indice); }
    public void marcarEtapa(int indice, boolean realizada) {
        validarIndice(indice);
        realizadas.set(indice, realizada);
    }
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= etapas.size())
            throw new IllegalArgumentException("Selecione uma etapa válida.");
    }
    public int contarRealizadas() {
        int total = 0;
        for (boolean realizada : realizadas) if (realizada) total++;
        return total;
    }
    @Override
    public String getTipo() { return "Checklist"; }
    @Override
    public int calcularProgresso() { return contarRealizadas() * 100 / etapas.size(); }
    @Override
    public boolean estaConcluida() { return contarRealizadas() == etapas.size(); }
}
