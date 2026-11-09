package dev.karinatelles.clinica.medico;

import dev.karinatelles.clinica.comum.DadosEndereco;

public record DadosMedico(Long id, String nome, String email, String telefone, String crm,
                          Especialidade especialidade, DadosEndereco endereco, boolean ativo) {

    public DadosMedico(Medico m) {
        this(m.getId(), m.getNome(), m.getEmail(), m.getTelefone(), m.getCrm(),
                m.getEspecialidade(), m.getEndereco().paraDados(), m.isAtivo());
    }
}
