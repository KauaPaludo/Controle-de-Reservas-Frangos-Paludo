// util/Entrada.java
package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Converte e valida o que o usuário digita (console e tela usam as mesmas regras).
 * Todos os métodos lançam IllegalArgumentException com uma mensagem pronta para exibir.
 */
public final class Entrada {

    public static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Entrada() {
    }

    /** Telefone é opcional: vazio devolve "". Se preenchido, precisa ter 10 ou 11 dígitos. */
    public static String telefone(String texto) {
        String digitos = texto == null ? "" : texto.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return "";
        }
        if (digitos.length() != 10 && digitos.length() != 11) {
            throw new IllegalArgumentException(
                    "Telefone inválido. Use DDD + número (10 ou 11 dígitos) ou deixe em branco.");
        }
        return digitos;
    }

    /** Peso em kg; aceita vírgula ou ponto (1,5 ou 1.5). */
    public static double peso(String texto) {
        try {
            double peso = Double.parseDouble(texto.trim().replace(',', '.'));
            if (!(peso > 0) || peso > 999) {
                throw new IllegalArgumentException("O peso deve ser maior que zero (em kg).");
            }
            return peso;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Peso inválido. Digite um número, por exemplo 1,5.");
        }
    }


    /** Quantidade de frangos em múltiplos de 0,5 (1, 1,5, 2...). Aceita vírgula ou ponto. */
    public static double unidades(String texto) {
        try {
            double quant = Double.parseDouble(texto.trim().replace(',', '.'));
            if (quant <= 0 || quant > 999) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }
            if (quant * 2 != Math.rint(quant * 2)) {
                throw new IllegalArgumentException("Use números inteiros ou meios, por exemplo 1, 1,5 ou 2.");
            }
            return quant;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Quantidade inválida. Digite um número, por exemplo 1 ou 1,5.");
        }
    }

    public static LocalDate data(String texto) {
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd/MM/aaaa, por exemplo 25/12/2026.");
        }
    }

    public static LocalTime hora(String texto) {
        try {
            return LocalTime.parse(texto.trim(), HORA);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Horário inválido. Use o formato HH:mm, por exemplo 18:30.");
        }
    }
}
