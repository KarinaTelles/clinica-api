package dev.karinatelles.clinica.auth;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * O segredo vem sempre de variável de ambiente (JWT_SECRET). Se faltar ou for
 * curto demais para HS256, a aplicação falha na subida com mensagem clara.
 */
@Validated
@ConfigurationProperties("app.jwt")
public record JwtProperties(
        @NotBlank(message = "defina a variável de ambiente JWT_SECRET")
        @Size(min = 32, message = "JWT_SECRET precisa ter pelo menos 32 caracteres")
        String secret,
        @NotNull Duration validade) {
}
