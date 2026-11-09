package dev.karinatelles.clinica.medico;

import dev.karinatelles.clinica.comum.DadosEndereco;
import jakarta.validation.Valid;

public record DadosAtualizacaoMedico(String nome, String telefone, @Valid DadosEndereco endereco) {
}
