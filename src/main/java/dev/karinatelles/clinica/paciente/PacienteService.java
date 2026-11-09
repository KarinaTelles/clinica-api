package dev.karinatelles.clinica.paciente;

import dev.karinatelles.clinica.erro.ConflitoException;
import dev.karinatelles.clinica.erro.NaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Paciente cadastrar(DadosCadastroPaciente dados) {
        if (repository.existsByCpf(CpfValidator.somenteDigitos(dados.cpf()))) {
            throw new ConflitoException("Já existe paciente cadastrado com este CPF");
        }
        return repository.save(new Paciente(dados));
    }

    @Transactional(readOnly = true)
    public Page<Paciente> listarAtivos(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao);
    }

    @Transactional(readOnly = true)
    public Paciente buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Paciente " + id + " não encontrado"));
    }

    @Transactional
    public Paciente atualizar(Long id, DadosAtualizacaoPaciente dados) {
        var paciente = buscar(id);
        paciente.atualizar(dados);
        return paciente;
    }

    @Transactional
    public void inativar(Long id) {
        buscar(id).inativar();
    }
}
