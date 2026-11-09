package dev.karinatelles.clinica.paciente;

import dev.karinatelles.clinica.comum.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroPaciente(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String telefone,
        @NotBlank @Cpf String cpf,
        @NotNull @Valid DadosEndereco endereco) {
}
