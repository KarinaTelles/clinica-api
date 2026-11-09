package dev.karinatelles.clinica.consulta.validacao;

import java.time.LocalDateTime;

import dev.karinatelles.clinica.medico.Medico;
import dev.karinatelles.clinica.paciente.Paciente;

/** Agendamento já com médico e paciente carregados, pronto para ser validado. */
public record PedidoAgendamento(Medico medico, Paciente paciente, LocalDateTime dataHora) {
}
