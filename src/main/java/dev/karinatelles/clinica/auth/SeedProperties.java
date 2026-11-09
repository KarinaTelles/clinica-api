package dev.karinatelles.clinica.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.seed")
public record SeedProperties(String login, String senha) {
}
