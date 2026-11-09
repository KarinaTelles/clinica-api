package dev.karinatelles.clinica.consulta;

import jakarta.validation.constraints.NotNull;

public record DadosCancelamento(@NotNull(message = "informe o motivo do cancelamento") MotivoCancelamento motivo) {
}
