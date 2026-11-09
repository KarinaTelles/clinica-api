package dev.karinatelles.clinica.medico;

import dev.karinatelles.clinica.erro.ConflitoException;
import dev.karinatelles.clinica.erro.NaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {

    private final MedicoRepository repository;

    public MedicoService(MedicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Medico cadastrar(DadosCadastroMedico dados) {
        if (repository.existsByCrm(dados.crm())) {
            throw new ConflitoException("Já existe médico cadastrado com o CRM " + dados.crm());
        }
        return repository.save(new Medico(dados));
    }

    @Transactional(readOnly = true)
    public Page<Medico> listarAtivos(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao);
    }

    @Transactional(readOnly = true)
    public Medico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Médico " + id + " não encontrado"));
    }

    @Transactional
    public Medico atualizar(Long id, DadosAtualizacaoMedico dados) {
        var medico = buscar(id);
        medico.atualizar(dados);
        return medico;
    }

    @Transactional
    public void inativar(Long id) {
        buscar(id).inativar();
    }
}
