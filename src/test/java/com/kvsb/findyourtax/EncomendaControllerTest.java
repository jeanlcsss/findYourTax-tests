package com.kvsb.findyourtax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvsb.findyourtax.dto.EncomendaDTO;
import com.kvsb.findyourtax.entities.Encomenda;
import com.kvsb.findyourtax.services.EncomendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EncomendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EncomendaService service;

    private EncomendaDTO encomendaDTO;
    private Encomenda encomenda;
    private LocalDate dataAtual;

    @BeforeEach
    void setUp() {

        dataAtual = LocalDate.of(2025, 7, 14);

        encomendaDTO = new EncomendaDTO(null, "Produto Teste", "Destinatário Teste", "01310100", "04538132", 10.0, null, null);

        encomenda = new Encomenda("Produto Teste", "Destinatário Teste", "01310100", "04538132", 10.0, null, null);
        encomenda.setId(1L);
    }

    @Test
    @DisplayName("CT_001: Deve retornar prazo de 1 dia e frete correto para CEPs com mesmo DDD")
    void calcularFrete_QuandoCEPsTemMesmoDDD_DeveRetornarSucesso() throws Exception {
        encomenda.setValorFrete(14.5); // 10.0 * 1.45
        encomenda.setDataEntrega(dataAtual.plusDays(1));
        when(service.calcularTransporte(any(EncomendaDTO.class))).thenReturn(encomenda);

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.valorFrete").value(14.5));
        result.andExpect(jsonPath("$.dataEntrega").value("2025-07-15"));
    }

    @Test
    @DisplayName("CT_002: Deve retornar prazo de 3 dias para CEPs na mesma UF e DDDs diferentes")
    void calcularFrete_QuandoCEPsMesmaUFDiferenteDDD_DeveRetornarSucesso() throws Exception {
        encomendaDTO.setCepDestino("13010002"); // Campinas (DDD 19)
        encomenda.setValorFrete(14.5);
        encomenda.setDataEntrega(dataAtual.plusDays(3));
        when(service.calcularTransporte(any(EncomendaDTO.class))).thenReturn(encomenda);

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.valorFrete").value(14.5));
        result.andExpect(jsonPath("$.dataEntrega").value("2025-07-17"));
    }

    @Test
    @DisplayName("CT_003: Deve retornar prazo de 10 dias para CEPs em UFs diferentes")
    void calcularFrete_QuandoCEPsUFDiferente_DeveRetornarSucesso() throws Exception {
        encomendaDTO.setCepDestino("22071000"); // Rio de Janeiro - RJ
        encomenda.setValorFrete(14.5);
        encomenda.setDataEntrega(dataAtual.plusDays(10));
        when(service.calcularTransporte(any(EncomendaDTO.class))).thenReturn(encomenda);

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.valorFrete").value(14.5));
        result.andExpect(jsonPath("$.dataEntrega").value("2025-07-24"));
    }

    @Test
    @DisplayName("CT_004: Deve retornar BadRequest para CEP com formato inválido")
    void calcularFrete_QuandoCEPFormatoInvalido_DeveRetornarBadRequest() throws Exception {
        encomendaDTO.setCepOrigem("12345-678");

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CT_005: Deve retornar BadRequest para peso com valor zero")
    void calcularFrete_QuandoPesoZero_DeveRetornarBadRequest() throws Exception {
        encomendaDTO.setPeso(0.0);

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CT_006: Deve retornar BadRequest para peso com valor negativo")
    void calcularFrete_QuandoPesoNegativo_DeveRetornarBadRequest() throws Exception {
        encomendaDTO.setPeso(-5.0);

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CT_007: Deve retornar BadRequest para nome do destinatário em branco")
    void calcularFrete_QuandoNomeDestinatarioEmBranco_DeveRetornarBadRequest() throws Exception {
        encomendaDTO.setNomeDestinatario("");

        ResultActions result = mockMvc.perform(post("/encomendas/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encomendaDTO)));

        result.andExpect(status().isBadRequest());
    }
}