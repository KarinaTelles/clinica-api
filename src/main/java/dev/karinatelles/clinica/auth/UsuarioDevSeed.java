package dev.karinatelles.clinica.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Só no perfil dev: cria um usuário com login e senha lidos de
 * DEV_USER_LOGIN e DEV_USER_PASSWORD. Nada é criado se elas não existirem.
 */
@Component
@Profile("dev")
class UsuarioDevSeed implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioDevSeed.class);

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final SeedProperties seed;

    UsuarioDevSeed(UsuarioRepository usuarios, PasswordEncoder encoder, SeedProperties seed) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.seed = seed;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (isBlank(seed.login()) || isBlank(seed.senha())) {
            log.warn("DEV_USER_LOGIN/DEV_USER_PASSWORD não definidas: nenhum usuário de desenvolvimento criado");
            return;
        }
        if (usuarios.findByLogin(seed.login()).isPresent()) {
            return;
        }
        usuarios.save(new Usuario(seed.login(), encoder.encode(seed.senha())));
        log.info("Usuário de desenvolvimento '{}' criado", seed.login());
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
