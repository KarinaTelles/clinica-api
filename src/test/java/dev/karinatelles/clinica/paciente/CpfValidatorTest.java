package dev.karinatelles.clinica.paciente;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CpfValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "529.982.247-25", "11144477735"})
    void aceitaCpfComDigitosCorretos(String cpf) {
        assertThat(CpfValidator.valido(cpf)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224724", "11111111111", "1234567890", "abc.def.ghi-jk", "529982247250"})
    void recusaCpfInvalido(String cpf) {
        assertThat(CpfValidator.valido(cpf)).isFalse();
    }
}
