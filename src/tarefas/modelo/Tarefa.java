package tarefas.modelo;

import java.time.LocalDate;

/** Dados comuns; cada tipo define como medir sua conclusão. */
public abstract class Tarefa {
    private final int codigo;
    private String titulo;
    private String responsavel;
    private String prioridade;
    private LocalDate prazo;

    protected Tarefa(int codigo, String titulo, String responsavel, String prioridade, LocalDate prazo) {
        if (codigo <= 0) throw new IllegalArgumentException("Código inválido.");
        this.codigo = codigo;
        atualizarDados(titulo, responsavel, prioridade, prazo);
    }

    // Uma alteração só é aplicada depois de todos os campos serem validados.
    public final void atualizarDados(String titulo, String responsavel, String prioridade, LocalDate prazo) {
        if (titulo == null || titulo.trim().isEmpty())
            throw new IllegalArgumentException("Informe o título da tarefa.");
        if (responsavel == null || responsavel.trim().isEmpty())
            throw new IllegalArgumentException("Informe o responsável.");
        if (!"Baixa".equals(prioridade) && !"Média".equals(prioridade) && !"Alta".equals(prioridade))
            throw new IllegalArgumentException("Selecione uma prioridade válida.");
        if (prazo == null) throw new IllegalArgumentException("Informe o prazo.");
        this.titulo = titulo.trim();
        this.responsavel = responsavel.trim();
        this.prioridade = prioridade;
        this.prazo = prazo;
    }

    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getResponsavel() { return responsavel; }
    public String getPrioridade() { return prioridade; }
    public LocalDate getPrazo() { return prazo; }
    public abstract String getTipo();
    public abstract int calcularProgresso();
    public abstract boolean estaConcluida();

    public boolean estaAtrasada(LocalDate hoje) {
        return !estaConcluida() && prazo.isBefore(hoje);
    }

    public String getSituacao(LocalDate hoje) {
        if (estaConcluida()) return "Concluída";
        if (estaAtrasada(hoje)) return "Atrasada";
        return "Pendente";
    }
}
