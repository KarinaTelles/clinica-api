package dev.karinatelles.clinica.consulta.validacao;

import dev.karinatelles.clinica.consulta.ConsultaRepository;
import dev.karinatelles.clinica.erro.ConflitoException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class MedicoSemOutraConsultaNoHorario implements ValidadorAgendamento {

    private final ConsultaRepository consultas;

    public MedicoSemOutraConsultaNoHorario(ConsultaRepository consultas) {
        this.consultas = consultas;
    }

    @Override
    public void validar(PedidoAgendamento pedido) {
        if (consultas.existsByMedicoIdAndDataHoraAndMotivoCancelamentoIsNull(pedido.medico().getId(), pedido.dataHora())) {
            throw new ConflitoException("O médico já tem consulta marcada neste horário");
        }
    }
}
