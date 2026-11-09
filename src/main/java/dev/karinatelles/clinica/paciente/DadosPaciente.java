package dev.karinatelles.clinica.paciente;

import dev.karinatelles.clinica.comum.DadosEndereco;

public record DadosPaciente(Long id, String nome, String email, String telefone, String cpf,
                            DadosEndereco endereco, boolean ativo) {

    public DadosPaciente(Paciente p) {
        this(p.getId(), p.getNome(), p.getEmail(), p.getTelefone(), p.getCpf(), p.getEndereco().paraDados(), p.isAtivo());
    }
}
