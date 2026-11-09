package dev.karinatelles.clinica.consulta;

import java.time.LocalDateTime;

import dev.karinatelles.clinica.medico.Especialidade;
import jakarta.validation.constraints.NotNull;

/**
 * Pedido de agendamento. Se {@code medicoId} vier vazio, a especialidade é
 * obrigatória e a clínica escolhe um médico livre.
 */
public record DadosAgendamento(
        Long medicoId,
        @NotNull Long pacienteId,
        @NotNull LocalDateTime dataHora,
        Especialidade especialidade) {
}
