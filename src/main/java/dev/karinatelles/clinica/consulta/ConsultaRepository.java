package dev.karinatelles.clinica.consulta;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByMedicoIdAndDataHoraAndMotivoCancelamentoIsNull(Long medicoId, LocalDateTime dataHora);

    boolean existsByPacienteIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
            Long pacienteId, LocalDateTime inicio, LocalDateTime fim);

    @EntityGraph(attributePaths = {"medico", "paciente"})
    Page<Consulta> findAllByMotivoCancelamentoIsNull(Pageable paginacao);

    @EntityGraph(attributePaths = {"medico", "paciente"})
    Page<Consulta> findAllByMedicoIdAndMotivoCancelamentoIsNull(Long medicoId, Pageable paginacao);
}
