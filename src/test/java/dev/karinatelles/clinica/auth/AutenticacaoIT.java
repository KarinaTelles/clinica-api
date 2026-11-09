package dev.karinatelles.clinica.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import dev.karinatelles.clinica.support.IntegracaoBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class AutenticacaoIT extends IntegracaoBase {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void criarUsuario() {
        usuarios.save(new Usuario("recepcao", encoder.encode("senha-local-de-teste")));
    }

    @Test
    void semTokenDevolve401() throws Exception {
        mvc.perform(get("/medicos")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginDevolveTokenQueAbreAsRotas() throws Exception {
        var resposta = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"recepcao","senha":"senha-local-de-teste"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(resposta, "$.token");

        mvc.perform(get("/medicos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void senhaErradaDevolve401() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"recepcao","senha":"errada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Não autenticado"));
    }

    @Test
    void tokenAdulteradoDevolve401() throws Exception {
        mvc.perform(get("/medicos").header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.e30.assinatura-falsa"))
                .andExpect(status().isUnauthorized());
    }
}
