package com.kvsb.findyourtax;

import com.kvsb.findyourtax.exceptions.ViaCepException;
import com.kvsb.findyourtax.exceptions.ViaCepFormatException;
import com.kvsb.findyourtax.utils.CepUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de Unidade para a classe utilitária CepUtils.
 * Como é uma classe com métodos estáticos e sem dependências,
 * não precisamos de Mockito nem de Spring. É um teste JUnit puro.
 */
class CepUtilsTest {

    @Test
    @DisplayName("validaCep não deve lançar exceção para CEP válido")
    void validaCep_QuandoCepValido_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> CepUtils.validaCep("12345678"));
    }

    @Test
    @DisplayName("validaCep deve lançar ViaCepException para CEP nulo")
    void validaCep_QuandoCepNulo_DeveLancarViaCepException() {
        assertThrows(ViaCepException.class, () -> CepUtils.validaCep(null));
    }

    @Test
    @DisplayName("validaCep deve lançar ViaCepException para CEP vazio")
    void validaCep_QuandoCepVazio_DeveLancarViaCepException() {
        assertThrows(ViaCepException.class, () -> CepUtils.validaCep(""));
    }

    @Test
    @DisplayName("validaCep deve lançar ViaCepException para CEP com menos de 8 dígitos")
    void validaCep_QuandoCepMenorQue8_DeveLancarViaCepException() {
        assertThrows(ViaCepException.class, () -> CepUtils.validaCep("1234567"));
    }

    @Test
    @DisplayName("validaCep deve lançar ViaCepFormatException para CEP com mais de 8 dígitos")
    void validaCep_QuandoCepMaiorQue8_DeveLancarViaCepFormatException() {
        assertThrows(ViaCepFormatException.class, () -> CepUtils.validaCep("123456789"));
    }
}
