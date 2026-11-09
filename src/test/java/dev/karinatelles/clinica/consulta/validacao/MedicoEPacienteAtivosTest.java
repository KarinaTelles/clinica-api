package dev.karinatelles.clinica.consulta.validacao;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import dev.karinatelles.clinica.erro.RegraNegocioException;
import dev.karinatelles.clinica.medico.Especialidade;
import dev.karinatelles.clinica.medico.Medico;
import dev.karinatelles.clinica.paciente.Paciente;
import dev.karinatelles.clinica.support.Fixtures;
import org.junit.jupiter.api.Test;

class MedicoEPacienteAtivosTest {

    private final MedicoEPacienteAtivos validador = new MedicoEPacienteAtivos();
    private final LocalDateTime horario = LocalDateTime.parse("2026-11-16T10:00");

    @Test
    void aceitaAmbosAtivos() {
        assertThatCode(() -> validador.validar(new PedidoAgendamento(medico(), paciente(), horario)))
                .doesNotThrowAnyException();
    }

    @Test
    void recusaMedicoInativo() {
        var medico = medico();
        medico.inativar();
        assertThatThrownBy(() -> validador.validar(new PedidoAgendamento(medico, paciente(), horario)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("médico");
    }

    @Test
    void recusaPacienteInativo() {
        var paciente = paciente();
        paciente.inativar();
        assertThatThrownBy(() -> validador.validar(new PedidoAgendamento(medico(), paciente, horario)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("paciente");
    }

    private static Medico medico() {
        return new Medico(Fixtures.medico(Especialidade.CARDIOLOGIA));
    }

    private static Paciente paciente() {
        return new Paciente(Fixtures.paciente());
    }
}
