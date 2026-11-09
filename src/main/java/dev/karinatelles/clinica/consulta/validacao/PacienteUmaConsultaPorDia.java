package dev.karinatelles.clinica.consulta.validacao;

import java.time.LocalTime;

import dev.karinatelles.clinica.consulta.ConsultaRepository;
import dev.karinatelles.clinica.erro.ConflitoException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class PacienteUmaConsultaPorDia implements ValidadorAgendamento {

    private final ConsultaRepository consultas;

    public PacienteUmaConsultaPorDia(ConsultaRepository consultas) {
        this.consultas = consultas;
    }

    @Override
    public void validar(PedidoAgendamento pedido) {
        var dia = pedido.dataHora().toLocalDate();
        boolean jaTem = consultas.existsByPacienteIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
                pedido.paciente().getId(), dia.atStartOfDay(), dia.atTime(LocalTime.MAX));
        if (jaTem) {
            throw new ConflitoException("O paciente já tem uma consulta marcada neste dia");
        }
    }
}
