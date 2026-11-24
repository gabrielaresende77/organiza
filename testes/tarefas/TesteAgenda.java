package tarefas;

import tarefas.modelo.*;
import tarefas.servico.AgendaTarefas;
import tarefas.util.Datas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Testes de comportamento, sem dependências externas. */
public class TesteAgenda {
    private static int verificacoes;
    private static void conferir(boolean valor) {
        verificacoes++;
        if (!valor) throw new AssertionError("Falhou a verificação " + verificacoes);
    }
    private static void rejeitar(Runnable acao) {
        boolean rejeitou = false;
        try { acao.run(); } catch (IllegalArgumentException erro) { rejeitou = true; }
        conferir(rejeitou);
    }
    public static void main(String[] args) {
        LocalDate hoje = LocalDate.of(2026, 11, 24);
        AgendaTarefas agenda = new AgendaTarefas();
        conferir(agenda.getTotal() == 0);
        agenda.cadastrarSimples("Enviar trabalho", "Gabi", "Alta", hoje);
        agenda.cadastrarChecklist("Preparar apresentação", "Ana", "Média", hoje.minusDays(1),
                Arrays.asList("Revisar conteúdo", "Preparar slides", "Ensaiar"));
        conferir(agenda.getTotal() == 2);
        Tarefa simples = agenda.localizar(1);
        Tarefa checklist = agenda.localizar(2);
        conferir(simples.calcularProgresso() == 0 && !simples.estaConcluida());
        conferir(simples.getSituacao(hoje).equals("Pendente"));
        conferir(checklist.getSituacao(hoje).equals("Atrasada"));
        conferir(agenda.contarAtrasadas(hoje) == 1);
        conferir(agenda.consultar("GABI", "Todas", hoje).size() == 1);
        conferir(agenda.consultar("apresentação", "Todas", hoje).size() == 1);
        conferir(agenda.consultar("", "Atrasada", hoje).size() == 1);
        conferir(agenda.consultar("Gabi", "Atrasada", hoje).isEmpty());
        conferir(agenda.consultar("nada", "Todas", hoje).isEmpty());
        agenda.consultar("", "Todas", hoje).clear();
        conferir(agenda.getTotal() == 2);
        ((TarefaSimples) simples).concluir();
        conferir(simples.calcularProgresso() == 100 && simples.estaConcluida());
        conferir(agenda.contarConcluidas() == 1);
        ((TarefaSimples) simples).reabrir();
        conferir(!simples.estaConcluida());
        TarefaChecklist etapas = (TarefaChecklist) checklist;
        etapas.marcarEtapa(0, true);
        conferir(checklist.calcularProgresso() == 33 && !checklist.estaConcluida());
        etapas.marcarEtapa(1, true);
        conferir(checklist.calcularProgresso() == 66);
        etapas.marcarEtapa(2, true);
        conferir(checklist.calcularProgresso() == 100 && checklist.estaConcluida());
        conferir(checklist.getSituacao(hoje).equals("Concluída"));
        conferir(agenda.contarAtrasadas(hoje) == 0);
        conferir(agenda.consultar("", "Concluída", hoje).size() == 1);
        etapas.marcarEtapa(1, false);
        conferir(!checklist.estaConcluida() && checklist.estaAtrasada(hoje));
        rejeitar(() -> etapas.marcarEtapa(99, true));
        rejeitar(() -> etapas.getEtapa(-1));
        agenda.alterar(2, "Revisar apresentação", "Gabi", "Alta", hoje.plusDays(1));
        conferir(checklist.getTitulo().equals("Revisar apresentação"));
        conferir(checklist.getResponsavel().equals("Gabi") && checklist.getPrioridade().equals("Alta"));
        conferir(checklist.getSituacao(hoje).equals("Pendente"));
        conferir(checklist.calcularProgresso() == 66);
        rejeitar(() -> agenda.alterar(2, "Mudança", " ", "Alta", hoje));
        conferir(checklist.getTitulo().equals("Revisar apresentação"));
        rejeitar(() -> agenda.cadastrarSimples(" ", "Gabi", "Alta", hoje));
        rejeitar(() -> agenda.cadastrarSimples("X", "Gabi", "Urgente", hoje));
        rejeitar(() -> agenda.cadastrarSimples("X", "Gabi", "Alta", null));
        rejeitar(() -> agenda.cadastrarChecklist("X", "Gabi", "Alta", hoje, Collections.emptyList()));
        rejeitar(() -> agenda.cadastrarChecklist("X", "Gabi", "Alta", hoje, Arrays.asList(" ")));
        agenda.excluir(1);
        conferir(agenda.getTotal() == 1);
        rejeitar(() -> agenda.localizar(1));
        rejeitar(() -> agenda.excluir(99));
        rejeitar(() -> agenda.alterar(99, "X", "Gabi", "Alta", hoje));
        agenda.cadastrarSimples("Nova", "Gabi", "Baixa", hoje);
        conferir(agenda.localizar(3).getCodigo() == 3);
        conferir(agenda.localizar(3).getSituacao(hoje.plusDays(1)).equals("Atrasada"));
        ((TarefaSimples) agenda.localizar(3)).concluir();
        conferir(!agenda.localizar(3).estaAtrasada(hoje.plusDays(1)));
        List<String> origem = new ArrayList<>(Arrays.asList("Etapa única"));
        TarefaChecklist unica = new TarefaChecklist(4, "X", "Y", "Baixa", hoje, origem);
        origem.clear();
        conferir(unica.getQuantidadeEtapas() == 1);
        unica.marcarEtapa(0, true);
        conferir(unica.calcularProgresso() == 100 && unica.estaConcluida());
        conferir(Datas.ler("29/02/2024").equals(LocalDate.of(2024, 2, 29)));
        conferir(Datas.formatar(hoje).equals("24/11/2026"));
        for (String entrada : new String[]{"31/02/2026", "29/02/2025", "1/1/2026", "texto", "", "01/01/0000"})
            rejeitar(() -> Datas.ler(entrada));
        System.out.println("SUCESSO: " + verificacoes + " verificações passaram.");
    }
}
