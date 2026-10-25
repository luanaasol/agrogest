package mz.isctem.agrogest.util;

import mz.isctem.agrogest.service.NegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Funcoes auxiliares para validar e converter os campos dos formularios. */
public final class Validacao {

    private Validacao() {
    }

    public static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NegocioException("O campo \"" + campo + "\" é obrigatório.");
        }
        return valor.trim();
    }

    public static String opcional(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public static int inteiro(String valor, String campo) {
        try {
            return Integer.parseInt(obrigatorio(valor, campo));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + campo + "\" deve ser um número inteiro.");
        }
    }

    public static int inteiroPositivo(String valor, String campo) {
        int v = inteiro(valor, campo);
        if (v <= 0) throw new NegocioException("O campo \"" + campo + "\" deve ser maior que zero.");
        return v;
    }

    public static int inteiroNaoNegativo(String valor, String campo) {
        int v = inteiro(valor, campo);
        if (v < 0) throw new NegocioException("O campo \"" + campo + "\" não pode ser negativo.");
        return v;
    }

    public static double decimalPositivo(String valor, String campo) {
        try {
            double v = Double.parseDouble(obrigatorio(valor, campo).replace(',', '.'));
            if (v <= 0) throw new NegocioException("O campo \"" + campo + "\" deve ser maior que zero.");
            return v;
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + campo + "\" deve ser um número.");
        }
    }

    public static BigDecimal dinheiro(String valor, String campo) {
        try {
            BigDecimal v = new BigDecimal(obrigatorio(valor, campo).replace(',', '.'));
            if (v.signum() <= 0) throw new NegocioException("O campo \"" + campo + "\" deve ser maior que zero.");
            return v;
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + campo + "\" deve ser um valor numérico.");
        }
    }

    /** Datas vindas de input type="date" (formato AAAA-MM-DD). */
    public static LocalDate data(String valor, String campo) {
        try {
            return LocalDate.parse(obrigatorio(valor, campo));
        } catch (DateTimeParseException e) {
            throw new NegocioException("O campo \"" + campo + "\" tem uma data inválida.");
        }
    }

    public static <E extends Enum<E>> E enumeracao(Class<E> tipo, String valor, String campo) {
        try {
            return Enum.valueOf(tipo, obrigatorio(valor, campo));
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Valor inválido para \"" + campo + "\".");
        }
    }
}
