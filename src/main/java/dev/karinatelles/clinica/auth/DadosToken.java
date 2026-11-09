package dev.karinatelles.clinica.auth;

import java.time.Instant;

public record DadosToken(String token, String tipo, Instant expiraEm) {
}
