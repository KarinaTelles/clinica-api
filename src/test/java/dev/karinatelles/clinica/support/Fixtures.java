package dev.karinatelles.clinica.support;

import java.util.concurrent.atomic.AtomicInteger;

import dev.karinatelles.clinica.comum.DadosEndereco;
import dev.karinatelles.clinica.medico.DadosCadastroMedico;
import dev.karinatelles.clinica.medico.Especialidade;
import dev.karinatelles.clinica.paciente.DadosCadastroPaciente;

public final class Fixtures {

    private static final AtomicInteger SEQUENCIA = new AtomicInteger(100_000);

    private Fixtures() {
    }

    public static DadosEndereco endereco() {
        return new DadosEndereco("Rua das Flores", "10", null, "Centro", "Belo Horizonte", "MG", "30110-000");
    }

    public static DadosCadastroMedico medico(Especialidade especialidade) {
        var n = SEQUENCIA.incrementAndGet();
        return new DadosCadastroMedico("Médico " + n, "medico" + n + "@clinica.dev", "31999990000",
                String.valueOf(n), especialidade, endereco());
    }

    public static DadosCadastroPaciente paciente() {
        var n = SEQUENCIA.incrementAndGet();
        return new DadosCadastroPaciente("Paciente " + n, "paciente" + n + "@exemplo.dev", "31988880000",
                cpfValido(n), endereco());
    }

    /** Gera um CPF com dígitos verificadores corretos a partir de um número. */
    public static String cpfValido(int semente) {
        var base = String.format("%09d", semente % 1_000_000_000);
        var d1 = digito(base, 10);
        var d2 = digito(base + d1, 11);
        return base + d1 + d2;
    }

    private static int digito(String parcial, int peso) {
        int soma = 0;
        for (int i = 0; i < parcial.length(); i++) {
            soma += (parcial.charAt(i) - '0') * (peso - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
