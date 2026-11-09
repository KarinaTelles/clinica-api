package dev.karinatelles.clinica.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * As regras de horário usam o relógio da clínica, não o do servidor.
 * Nos testes de unidade o relógio é fixo.
 */
@Configuration
public class RelogioConfig {

    @Bean
    Clock relogio(@Value("${app.fuso-horario}") String fuso) {
        return Clock.system(ZoneId.of(fuso));
    }
}
