package dev.karinatelles.clinica.consulta;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import dev.karinatelles.clinica.consulta.validacao.PedidoAgendamento;
import dev.karinatelles.clinica.consulta.validacao.ValidadorAgendamento;
import dev.karinatelles.clinica.erro.ConflitoException;
import dev.karinatelles.clinica.erro.NaoEncontradoException;
import dev.karinatelles.clinica.erro.RegraNegocioException;
import dev.karinatelles.clinica.medico.Medico;
import dev.karinatelles.clinica.medico.MedicoRepository;
import dev.karinatelles.clinica.paciente.PacienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaService {

    static final Duration ANTECEDENCIA_CANCELAMENTO = Duration.ofHours(24);

    private final ConsultaRepository consultas;
    private final MedicoRepository medicos;
    private final PacienteRepository pacientes;
    private final List<ValidadorAgendamento> validadores;
    private final Clock relogio;

    public ConsultaService(ConsultaRepository consultas, MedicoRepository medicos, PacienteRepository pacientes,
                           List<ValidadorAgendamento> validadores, Clock relogio) {
        this.consultas = consultas;
        this.medicos = medicos;
        this.pacientes = pacientes;
        this.validadores = validadores;
        this.relogio = relogio;
    }

    @Transactional
    public DadosConsulta agendar(DadosAgendamento dados) {
        var paciente = pacientes.findById(dados.pacienteId())
                .orElseThrow(() -> new NaoEncontradoException("Paciente " + dados.pacienteId() + " não encontrado"));
        var medico = escolherMedico(dados);

        var pedido = new PedidoAgendamento(medico, paciente, dados.dataHora());
        validadores.forEach(v -> v.validar(pedido));

        return new DadosConsulta(consultas.save(new Consulta(medico, paciente, dados.dataHora())));
    }

    private Medico escolherMedico(DadosAgendamento dados) {
        if (dados.medicoId() != null) {
            var medico = medicos.findById(dados.medicoId())
                    .orElseThrow(() -> new NaoEncontradoException("Médico " + dados.medicoId() + " não encontrado"));
            if (dados.especialidade() != null && dados.especialidade() != medico.getEspecialidade()) {
                throw new RegraNegocioException("O médico escolhido não atende a especialidade " + dados.especialidade());
            }
            return medico;
        }
        if (dados.especialidade() == null) {
            throw new RegraNegocioException("Informe o médico ou a especialidade");
        }
        return medicos.livresNoHorario(dados.especialidade(), dados.dataHora(), PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new ConflitoException(
                        "Nenhum médico de " + dados.especialidade() + " livre neste horário"));
    }

    @Transactional
    public DadosConsulta cancelar(Long id, DadosCancelamento dados) {
        var consulta = buscar(id);
        if (consulta.isCancelada()) {
            throw new RegraNegocioException("A consulta já está cancelada");
        }
        var agora = LocalDateTime.now(relogio);
        if (Duration.between(agora, consulta.getDataHora()).compareTo(ANTECEDENCIA_CANCELAMENTO) < 0) {
            throw new RegraNegocioException("O cancelamento precisa ser feito com pelo menos 24 horas de antecedência");
        }
        consulta.cancelar(dados.motivo(), agora);
        return new DadosConsulta(consulta);
    }

    private Consulta buscar(Long id) {
        return consultas.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Consulta " + id + " não encontrada"));
    }

    @Transactional(readOnly = true)
    public Page<DadosConsulta> listarAtivas(Long medicoId, Pageable paginacao) {
        var pagina = medicoId == null
                ? consultas.findAllByMotivoCancelamentoIsNull(paginacao)
                : consultas.findAllByMedicoIdAndMotivoCancelamentoIsNull(medicoId, paginacao);
        return pagina.map(DadosConsulta::new);
    }

    @Transactional(readOnly = true)
    public DadosConsulta detalhar(Long id) {
        return new DadosConsulta(buscar(id));
    }
}
