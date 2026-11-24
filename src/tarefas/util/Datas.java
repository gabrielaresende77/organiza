package tarefas.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class Datas {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private Datas() { }
    public static String formatar(LocalDate data) { return FORMATO.format(data); }
    public static LocalDate ler(String texto) {
        String valor = texto == null ? "" : texto.trim();
        try {
            if (!valor.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}"))
                throw new DateTimeParseException("Formato", valor, 0);
            LocalDate data = LocalDate.parse(valor, FORMATO);
            if (data.getYear() < 1) throw new DateTimeParseException("Ano", valor, 0);
            return data;
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException("Informe uma data existente no formato dd/mm/aaaa.", erro);
        }
    }
}
