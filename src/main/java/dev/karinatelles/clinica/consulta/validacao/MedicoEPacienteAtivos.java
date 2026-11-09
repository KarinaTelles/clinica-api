package dev.karinatelles.clinica.consulta.validacao;

import dev.karinatelles.clinica.erro.RegraNegocioException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class MedicoEPacienteAtivos implements ValidadorAgendamento {

    @Override
    public void validar(PedidoAgendamento pedido) {
        if (!pedido.medico().isAtivo()) {
            throw new RegraNegocioException("O médico está inativo e não recebe novas consultas");
        }
        if (!pedido.paciente().isAtivo()) {
            throw new RegraNegocioException("O paciente está inativo e não pode agendar consultas");
        }
    }
}
