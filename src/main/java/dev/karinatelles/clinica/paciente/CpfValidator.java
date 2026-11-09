package dev.karinatelles.clinica.paciente;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        // nulo fica a cargo do @NotBlank
        return valor == null || valido(valor);
    }

    public static String somenteDigitos(String cpf) {
        return cpf.replaceAll("[.\\-\\s]", "");
    }

    public static boolean valido(String cpf) {
        var digitos = somenteDigitos(cpf);
        if (!digitos.matches("\\d{11}") || digitos.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(digitos, 9) == digitos.charAt(9) - '0'
                && digitoVerificador(digitos, 10) == digitos.charAt(10) - '0';
    }

    private static int digitoVerificador(String digitos, int posicao) {
        int soma = 0;
        for (int i = 0; i < posicao; i++) {
            soma += (digitos.charAt(i) - '0') * (posicao + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
