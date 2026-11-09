package dev.karinatelles.clinica.auth;

import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, TokenService tokens) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public DadosToken login(@RequestBody @Valid DadosLogin dados) {
        return usuarios.findByLogin(dados.login())
                .filter(u -> encoder.matches(dados.senha(), u.getSenhaHash()))
                .map(u -> tokens.gerar(u.getLogin()))
                .orElseThrow(() -> new BadCredentialsException("Login ou senha inválidos"));
    }
}
