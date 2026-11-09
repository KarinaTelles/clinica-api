package dev.karinatelles.clinica.comum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosEndereco(
        @NotBlank String logradouro,
        String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cidade,
        @NotBlank @Pattern(regexp = "[A-Za-z]{2}", message = "deve ser a sigla do estado, com 2 letras") String uf,
        @NotBlank @Pattern(regexp = "\\d{5}-?\\d{3}", message = "deve estar no formato 00000-000") String cep) {
}
