package dev.karinatelles.clinica;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.karinatelles.clinica.support.Fixtures;
import dev.karinatelles.clinica.support.IntegracaoBase;
import org.junit.jupiter.api.Test;

class CadastroIT extends IntegracaoBase {

    @Test
    void listaSoMedicosAtivosPaginado() throws Exception {
        criarMedico("CARDIOLOGIA", "1111");
        criarMedico("PEDIATRIA", "2222");
        var inativo = criarMedico("ORTOPEDIA", "3333");
        mvc.perform(delete("/medicos/{id}", inativo).with(jwt())).andExpect(status().isNoContent());

        mvc.perform(get("/medicos").param("size", "1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(2));

        mvc.perform(get("/medicos/{id}", inativo).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void recusaCrmRepetido() throws Exception {
        criarMedico("CARDIOLOGIA", "1111");
        var json = """
                {"nome":"Outro","email":"outro@clinica.dev","telefone":"1","crm":"1111","especialidade":"PEDIATRIA","endereco":%s}
                """.formatted(ENDERECO);
        enviar(post("/medicos"), json).andExpect(status().isConflict());
    }

    @Test
    void atualizaTelefoneDoMedico() throws Exception {
        var id = criarMedico("CARDIOLOGIA", "1111");
        enviar(put("/medicos/{id}", id), """
                {"telefone":"31900000000"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefone").value("31900000000"))
                .andExpect(jsonPath("$.crm").value("1111"));
    }

    @Test
    void guardaCpfSemMascaraERecusaCpfInvalido() throws Exception {
        var cpf = Fixtures.cpfValido(42);
        var mascarado = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        var id = criarPaciente(mascarado);
        mvc.perform(get("/pacientes/{id}", id).with(jwt()))
                .andExpect(jsonPath("$.cpf").value(cpf));

        var json = """
                {"nome":"X","email":"x@exemplo.dev","telefone":"1","cpf":"12345678900","endereco":%s}
                """.formatted(ENDERECO);
        enviar(post("/pacientes"), json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("cpf"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("CPF inválido"));
    }

    @Test
    void recursoInexistenteDevolve404ComoProblemDetail() throws Exception {
        mvc.perform(get("/pacientes/999").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Paciente 999 não encontrado"));
    }
}
