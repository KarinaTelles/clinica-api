package dev.karinatelles.clinica.medico;

import dev.karinatelles.clinica.comum.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroMedico(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String telefone,
        @NotBlank @Pattern(regexp = "\\d{4,7}", message = "deve ter de 4 a 7 dígitos") String crm,
        @NotNull Especialidade especialidade,
        @NotNull @Valid DadosEndereco endereco) {
}
