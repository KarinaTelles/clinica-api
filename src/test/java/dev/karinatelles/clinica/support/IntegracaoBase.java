package dev.karinatelles.clinica.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

import com.jayway.jsonpath.JsonPath;
import dev.karinatelles.clinica.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Sobe a aplicação inteira contra um PostgreSQL de verdade (Testcontainers). */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class IntegracaoBase {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limparBanco() {
        jdbc.execute("truncate table consultas, medicos, pacientes, usuarios restart identity cascade");
    }

    /** Segunda-feira de daqui a duas semanas, no horário pedido, no fuso da clínica. */
    protected static LocalDateTime segundaQueVem(int hora) {
        return LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                .plusWeeks(2)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atTime(hora, 0);
    }

    protected ResultActions enviar(MockHttpServletRequestBuilder requisicao, String json) throws Exception {
        return mvc.perform(requisicao.with(jwt()).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected long criarMedico(String especialidade, String crm) throws Exception {
        var json = """
                {"nome":"Dra. Ana Souza","email":"ana%s@clinica.dev","telefone":"31999990000","crm":"%s",
                 "especialidade":"%s","endereco":%s}
                """.formatted(crm, crm, especialidade, ENDERECO);
        return idDe(enviar(post("/medicos"), json));
    }

    protected long criarPaciente(String cpf) throws Exception {
        var json = """
                {"nome":"João Lima","email":"joao%s@exemplo.dev","telefone":"31988880000","cpf":"%s","endereco":%s}
                """.formatted(cpf, cpf, ENDERECO);
        return idDe(enviar(post("/pacientes"), json));
    }

    protected static long idDe(ResultActions resultado) throws Exception {
        var corpo = resultado.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }

    protected static final String ENDERECO = """
            {"logradouro":"Rua das Flores","numero":"10","bairro":"Centro","cidade":"Belo Horizonte","uf":"MG","cep":"30110-000"}
            """;
}
