package dev.karinatelles.clinica.consulta.validacao;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import dev.karinatelles.clinica.erro.RegraNegocioException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HorarioDeFuncionamentoTest {

    private final HorarioDeFuncionamento validador = new HorarioDeFuncionamento();

    // 16/11/2026 é segunda-feira; 21/11 sábado; 22/11 domingo
    @ParameterizedTest
    @ValueSource(strings = {"2026-11-16T07:00", "2026-11-16T12:00", "2026-11-16T18:00", "2026-11-21T07:00"})
    void aceitaHoraCheiaDentroDoExpediente(String dataHora) {
        assertThatCode(() -> validador.validar(pedido(dataHora))).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-11-16T06:00", "2026-11-16T19:00", "2026-11-16T18:30", "2026-11-22T10:00"})
    void recusaForaDoExpediente(String dataHora) {
        assertThatThrownBy(() -> validador.validar(pedido(dataHora))).isInstanceOf(RegraNegocioException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-11-16T10:30", "2026-11-16T10:00:15"})
    void recusaHorarioQueNaoComecaNaHoraCheia(String dataHora) {
        assertThatThrownBy(() -> validador.validar(pedido(dataHora)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("hora cheia");
    }

    private static PedidoAgendamento pedido(String dataHora) {
        return new PedidoAgendamento(null, null, LocalDateTime.parse(dataHora));
    }
}
