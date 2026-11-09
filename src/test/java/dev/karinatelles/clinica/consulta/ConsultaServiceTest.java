package dev.karinatelles.clinica.consulta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import dev.karinatelles.clinica.consulta.validacao.ValidadorAgendamento;
import dev.karinatelles.clinica.erro.ConflitoException;
import dev.karinatelles.clinica.erro.RegraNegocioException;
import dev.karinatelles.clinica.medico.Especialidade;
import dev.karinatelles.clinica.medico.Medico;
import dev.karinatelles.clinica.medico.MedicoRepository;
import dev.karinatelles.clinica.paciente.Paciente;
import dev.karinatelles.clinica.paciente.PacienteRepository;
import dev.karinatelles.clinica.support.Fixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsultaServiceTest {

    private static final ZoneId SP = ZoneId.of("America/Sao_Paulo");
    private static final LocalDateTime AGORA = LocalDateTime.parse("2026-11-16T09:00");

    private final ConsultaRepository consultas = mock(ConsultaRepository.class);
    private final MedicoRepository medicos = mock(MedicoRepository.class);
    private final PacienteRepository pacientes = mock(PacienteRepository.class);
    private final ValidadorAgendamento validador = mock(ValidadorAgendamento.class);
    private ConsultaService service;

    private final Medico cardiologista = new Medico(Fixtures.medico(Especialidade.CARDIOLOGIA));
    private final Paciente paciente = new Paciente(Fixtures.paciente());

    @BeforeEach
    void setUp() {
        var relogio = Clock.fixed(AGORA.atZone(SP).toInstant(), SP);
        service = new ConsultaService(consultas, medicos, pacientes, List.of(validador), relogio);
        when(pacientes.findById(anyLong())).thenReturn(Optional.of(paciente));
        when(consultas.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void escolheMedicoLivreQuandoSoAEspecialidadeEInformada() {
        var horario = AGORA.plusDays(1);
        when(medicos.livresNoHorario(eq(Especialidade.CARDIOLOGIA), eq(horario), any()))
                .thenReturn(List.of(cardiologista));

        var consulta = service.agendar(new DadosAgendamento(null, 1L, horario, Especialidade.CARDIOLOGIA));

        assertThat(consulta.medicoNome()).isEqualTo(cardiologista.getNome());
        verify(validador).validar(any());
    }

    @Test
    void recusaQuandoNenhumMedicoDaEspecialidadeEstaLivre() {
        when(medicos.livresNoHorario(any(), any(), any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.agendar(new DadosAgendamento(null, 1L, AGORA.plusDays(1), Especialidade.ORTOPEDIA)))
                .isInstanceOf(ConflitoException.class);
        verify(consultas, never()).save(any());
    }

    @Test
    void exigeMedicoOuEspecialidade() {
        assertThatThrownBy(() -> service.agendar(new DadosAgendamento(null, 1L, AGORA.plusDays(1), null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("especialidade");
    }

    @Test
    void recusaEspecialidadeDiferenteDaDoMedicoEscolhido() {
        when(medicos.findById(7L)).thenReturn(Optional.of(cardiologista));

        assertThatThrownBy(() -> service.agendar(new DadosAgendamento(7L, 1L, AGORA.plusDays(1), Especialidade.PEDIATRIA)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void naoGravaQuandoUmValidadorRecusa() {
        when(medicos.findById(7L)).thenReturn(Optional.of(cardiologista));
        org.mockito.Mockito.doThrow(new ConflitoException("ocupado")).when(validador).validar(any());

        assertThatThrownBy(() -> service.agendar(new DadosAgendamento(7L, 1L, AGORA.plusDays(1), null)))
                .isInstanceOf(ConflitoException.class);
        verify(consultas, never()).save(any());
    }

    @Test
    void cancelaComMaisDe24HorasDeAntecedencia() {
        var consulta = new Consulta(cardiologista, paciente, AGORA.plusHours(24));
        when(consultas.findById(1L)).thenReturn(Optional.of(consulta));

        var cancelada = service.cancelar(1L, new DadosCancelamento(MotivoCancelamento.PACIENTE_DESISTIU));

        assertThat(cancelada.motivoCancelamento()).isEqualTo(MotivoCancelamento.PACIENTE_DESISTIU);
        assertThat(cancelada.canceladaEm()).isEqualTo(AGORA);
    }

    @Test
    void recusaCancelamentoComMenosDe24Horas() {
        var consulta = new Consulta(cardiologista, paciente, AGORA.plusHours(23).plusMinutes(59));
        when(consultas.findById(1L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> service.cancelar(1L, new DadosCancelamento(MotivoCancelamento.OUTROS)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("24 horas");
        assertThat(consulta.isCancelada()).isFalse();
    }

    @Test
    void recusaCancelarDuasVezes() {
        var consulta = new Consulta(cardiologista, paciente, AGORA.plusDays(3));
        consulta.cancelar(MotivoCancelamento.MEDICO_CANCELOU, AGORA);
        when(consultas.findById(1L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> service.cancelar(1L, new DadosCancelamento(MotivoCancelamento.OUTROS)))
                .isInstanceOf(RegraNegocioException.class);
    }
}
