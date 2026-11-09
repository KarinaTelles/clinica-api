package dev.karinatelles.clinica.medico;

public record DadosListagemMedico(Long id, String nome, String email, String crm, Especialidade especialidade) {

    public DadosListagemMedico(Medico m) {
        this(m.getId(), m.getNome(), m.getEmail(), m.getCrm(), m.getEspecialidade());
    }
}
