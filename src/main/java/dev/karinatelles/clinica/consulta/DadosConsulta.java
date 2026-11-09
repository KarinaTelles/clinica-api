package dev.karinatelles.clinica.consulta;

import java.time.LocalDateTime;

import dev.karinatelles.clinica.medico.Especialidade;

public record DadosConsulta(Long id, Long medicoId, String medicoNome, Especialidade especialidade,
                            Long pacienteId, String pacienteNome, LocalDateTime dataHora,
                            MotivoCancelamento motivoCancelamento, LocalDateTime canceladaEm) {

    public DadosConsulta(Consulta c) {
        this(c.getId(), c.getMedico().getId(), c.getMedico().getNome(), c.getMedico().getEspecialidade(),
                c.getPaciente().getId(), c.getPaciente().getNome(), c.getDataHora(),
                c.getMotivoCancelamento(), c.getCanceladaEm());
    }
}
