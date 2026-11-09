package dev.karinatelles.clinica;

import org.springframework.boot.SpringApplication;

public class TestClinicaApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(ClinicaApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
