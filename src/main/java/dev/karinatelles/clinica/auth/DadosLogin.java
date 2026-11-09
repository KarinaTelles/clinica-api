package dev.karinatelles.clinica.auth;

import jakarta.validation.constraints.NotBlank;

public record DadosLogin(@NotBlank String login, @NotBlank String senha) {
}
