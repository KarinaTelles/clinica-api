package dev.karinatelles.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClinicaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicaApiApplication.class, args);
    }
}
