package dev.karinatelles.clinica.consulta;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.karinatelles.clinica.support.Fixtures;
import dev.karinatelles.clinica.support.IntegracaoBase;
import org.junit.jupiter.api.Test;

class AgendamentoIT extends IntegracaoBase {

    @Test
    void agendaEListaConsulta() throws Exception {
        var medico = criarMedico("CARDIOLOGIA", "123456");
        var paciente = criarPaciente(Fixtures.cpfValido(1));

        enviar(post("/consultas"), agendamento(medico, paciente, segundaQueVem(10).toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicoId").value(medico))
                .andExpect(jsonPath("$.especialidade").value("CARDIOLOGIA"));

        mvc.perform(get("/consultas").param("medicoId", String.valueOf(medico)).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void recusaSegundaConsultaDoMedicoNoMesmoHorario() throws Exception {
        var medico = criarMedico("CARDIOLOGIA", "123456");
        var horario = segundaQueVem(10).toString();
        enviar(post("/consultas"), agendamento(medico, criarPaciente(Fixtures.cpfValido(1)), horario))
                .andExpect(status().isCreated());

        enviar(post("/consultas"), agendamento(medico, criarPaciente(Fixtures.cpfValido(2)), horario))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de agenda ou cadastro"))
                .andExpect(jsonPath("$.detail").value("O médico já tem consulta marcada neste horário"));
    }

    @Test
    void recusaSegundaConsultaDoPacienteNoMesmoDia() throws Exception {
        var paciente = criarPaciente(Fixtures.cpfValido(1));
        enviar(post("/consultas"), agendamento(criarMedico("CARDIOLOGIA", "1111"), paciente, segundaQueVem(9).toString()))
                .andExpect(status().isCreated());

        enviar(post("/consultas"), agendamento(criarMedico("ORTOPEDIA", "2222"), paciente, segundaQueVem(15).toString()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("O paciente já tem uma consulta marcada neste dia"));
    }

    @Test
    void escolheMedicoLivreDaEspecialidade() throws Exception {
        var ocupado = criarMedico("ORTOPEDIA", "1111");
        var livre = criarMedico("ORTOPEDIA", "2222");
        var horario = segundaQueVem(11).toString();
        enviar(post("/consultas"), agendamento(ocupado, criarPaciente(Fixtures.cpfValido(1)), horario))
                .andExpect(status().isCreated());

        var json = """
                {"pacienteId":%d,"dataHora":"%s","especialidade":"ORTOPEDIA"}
                """.formatted(criarPaciente(Fixtures.cpfValido(2)), horario);
        enviar(post("/consultas"), json)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicoId").value(livre));

        var outro = """
                {"pacienteId":%d,"dataHora":"%s","especialidade":"ORTOPEDIA"}
                """.formatted(criarPaciente(Fixtures.cpfValido(3)), horario);
        enviar(post("/consultas"), outro).andExpect(status().isConflict());
    }

    @Test
    void recusaHorarioForaDoExpediente() throws Exception {
        var medico = criarMedico("CARDIOLOGIA", "123456");
        var paciente = criarPaciente(Fixtures.cpfValido(1));

        enviar(post("/consultas"), agendamento(medico, paciente, segundaQueVem(19).toString()))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void recusaMedicoInativo() throws Exception {
        var medico = criarMedico("CARDIOLOGIA", "123456");
        mvc.perform(delete("/medicos/{id}", medico).with(jwt())).andExpect(status().isNoContent());

        enviar(post("/consultas"), agendamento(medico, criarPaciente(Fixtures.cpfValido(1)), segundaQueVem(10).toString()))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void cancelaComMotivoELiberaOHorario() throws Exception {
        var medico = criarMedico("CARDIOLOGIA", "123456");
        var horario = segundaQueVem(10).toString();
        var consulta = idDe(enviar(post("/consultas"), agendamento(medico, criarPaciente(Fixtures.cpfValido(1)), horario)));

        enviar(post("/consultas/{id}/cancelamento", consulta), "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("motivo"));

        enviar(post("/consultas/{id}/cancelamento", consulta), """
                {"motivo":"PACIENTE_DESISTIU"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivoCancelamento").value("PACIENTE_DESISTIU"));

        enviar(post("/consultas"), agendamento(medico, criarPaciente(Fixtures.cpfValido(2)), horario))
                .andExpect(status().isCreated());
    }

    private static String agendamento(long medico, long paciente, String dataHora) {
        return """
                {"medicoId":%d,"pacienteId":%d,"dataHora":"%s"}
                """.formatted(medico, paciente, dataHora);
    }
}
