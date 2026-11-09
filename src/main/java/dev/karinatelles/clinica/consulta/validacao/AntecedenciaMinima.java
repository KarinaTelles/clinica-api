package dev.karinatelles.clinica.consulta.validacao;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import dev.karinatelles.clinica.erro.RegraNegocioException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AntecedenciaMinima implements ValidadorAgendamento {

    static final Duration MINIMO = Duration.ofMinutes(30);

    private final Clock relogio;

    public AntecedenciaMinima(Clock relogio) {
        this.relogio = relogio;
    }

    @Override
    public void validar(PedidoAgendamento pedido) {
        var agora = LocalDateTime.now(relogio);
        if (Duration.between(agora, pedido.dataHora()).compareTo(MINIMO) < 0) {
            throw new RegraNegocioException("A consulta precisa ser marcada com pelo menos 30 minutos de antecedência");
        }
    }
}
