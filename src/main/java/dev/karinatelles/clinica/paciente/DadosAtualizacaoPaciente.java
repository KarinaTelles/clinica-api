package dev.karinatelles.clinica.paciente;

import dev.karinatelles.clinica.comum.DadosEndereco;
import jakarta.validation.Valid;

public record DadosAtualizacaoPaciente(String nome, String telefone, @Valid DadosEndereco endereco) {
}
