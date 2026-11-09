package dev.karinatelles.clinica.consulta.validacao;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import dev.karinatelles.clinica.erro.RegraNegocioException;
import org.junit.jupiter.api.Test;

class AntecedenciaMinimaTest {

    private static final ZoneId SP = ZoneId.of("America/Sao_Paulo");
    // 16/11/2026 09:40 em São Paulo
    private final Clock relogio = Clock.fixed(LocalDateTime.parse("2026-11-16T09:40").atZone(SP).toInstant(), SP);
    private final AntecedenciaMinima validador = new AntecedenciaMinima(relogio);

    @Test
    void aceitaComTrintaMinutosOuMais() {
        assertThatCode(() -> validador.validar(pedido("2026-11-16T10:10"))).doesNotThrowAnyException();
        assertThatCode(() -> validador.validar(pedido("2026-11-16T11:00"))).doesNotThrowAnyException();
    }

    @Test
    void recusaComMenosDeTrintaMinutos() {
        assertThatThrownBy(() -> validador.validar(pedido("2026-11-16T10:00")))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void recusaHorarioNoPassado() {
        assertThatThrownBy(() -> validador.validar(pedido("2026-11-16T09:00")))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void usaOFusoDaClinicaENaoODoServidor() {
        // Mesmo instante visto em UTC seria 12:40; em São Paulo ainda são 09:40.
        var utc = Clock.fixed(Instant.parse("2026-11-16T12:40:00Z"), SP);
        assertThatCode(() -> new AntecedenciaMinima(utc).validar(pedido("2026-11-16T11:00"))).doesNotThrowAnyException();
    }

    private static PedidoAgendamento pedido(String dataHora) {
        return new PedidoAgendamento(null, null, LocalDateTime.parse(dataHora));
    }
}
