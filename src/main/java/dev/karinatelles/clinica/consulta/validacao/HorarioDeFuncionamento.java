package dev.karinatelles.clinica.consulta.validacao;

import java.time.DayOfWeek;
import java.time.LocalTime;

import dev.karinatelles.clinica.erro.RegraNegocioException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * A clínica atende de segunda a sábado, das 07:00 às 19:00, em consultas de
 * 1 hora que começam sempre na hora cheia. A última começa às 18:00.
 */
@Component
@Order(1)
public class HorarioDeFuncionamento implements ValidadorAgendamento {

    static final LocalTime ABERTURA = LocalTime.of(7, 0);
    static final LocalTime ULTIMO_INICIO = LocalTime.of(18, 0);

    @Override
    public void validar(PedidoAgendamento pedido) {
        var dataHora = pedido.dataHora();
        if (dataHora.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RegraNegocioException("A clínica não abre aos domingos");
        }
        var hora = dataHora.toLocalTime();
        if (hora.isBefore(ABERTURA) || hora.isAfter(ULTIMO_INICIO)) {
            throw new RegraNegocioException("Consultas só podem começar entre 07:00 e 18:00");
        }
        if (dataHora.getMinute() != 0 || dataHora.getSecond() != 0 || dataHora.getNano() != 0) {
            throw new RegraNegocioException("Consultas duram 1 hora e começam na hora cheia (ex.: 10:00)");
        }
    }
}
