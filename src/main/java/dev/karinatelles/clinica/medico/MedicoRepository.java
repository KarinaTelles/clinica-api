package dev.karinatelles.clinica.medico;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findAllByAtivoTrue(Pageable paginacao);

    boolean existsByCrm(String crm);

    /**
     * Médicos ativos da especialidade que não têm consulta ativa no horário,
     * do menor id para o maior. O serviço usa o primeiro.
     */
    @Query("""
            select m from Medico m
            where m.ativo = true
              and m.especialidade = :especialidade
              and not exists (
                  select 1 from Consulta c
                  where c.medico = m
                    and c.dataHora = :dataHora
                    and c.motivoCancelamento is null)
            order by m.id
            """)
    List<Medico> livresNoHorario(Especialidade especialidade, LocalDateTime dataHora, Pageable limite);
}
