package com.kvsb.findyourtax;

import com.google.gson.Gson;
import com.kvsb.findyourtax.dto.CepDTO;
import com.kvsb.findyourtax.dto.EncomendaDTO;
import com.kvsb.findyourtax.entities.Encomenda;
import com.kvsb.findyourtax.entities.StatusEncomenda;
import com.kvsb.findyourtax.repositories.EncomendaRepository;
import com.kvsb.findyourtax.services.EncomendaService;
import com.kvsb.findyourtax.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste de Unidade para EncomendaService.
 * O objetivo é testar a lógica de negócio de forma isolada.
 * - @ExtendWith(MockitoExtension.class): Habilita o Mockito.
 * - @Mock: Cria um mock (simulação) para as dependências externas.
 * - @Spy: Cria um objeto "espião". Usamos um Spy no Gson para que ele funcione normalmente,
 * e um Spy no próprio Service para conseguirmos simular a chamada ao método findCep,
 * evitando uma chamada HTTP real durante o teste de unidade.
 * - @InjectMocks: Injeta os mocks criados na instância do nosso serviço.
 */
@ExtendWith(MockitoExtension.class)
public class EncomendaServiceTest {

    @Mock
    private EncomendaRepository repository;

    @Spy
    private Gson gson = new Gson();

    @InjectMocks
    @Spy
    private EncomendaService service;

    private EncomendaDTO encomendaDTO;
    private Encomenda encomenda;
    private CepDTO cepOrigemSP;
    private CepDTO cepDestinoSP;
    private CepDTO cepDestinoRJ;
    private Long existingId;
    private Long nonExistingId;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;

        encomendaDTO = new EncomendaDTO(null, "Produto Teste", "Destinatário Teste", "01310100", "04538132", 10.0, null, null);
        encomenda = new Encomenda("Produto Teste", "Destinatário Teste", "01310100", "04538132", 10.0, LocalDate.now(), 14.5);
        encomenda.setId(existingId);

        cepOrigemSP = new CepDTO("01310-100", "Avenida Paulista", "", "Bela Vista", "São Paulo", "SP", "", "", "11", "");
        cepDestinoSP = new CepDTO("04538-132", "Avenida Brigadeiro Faria Lima", "", "Itaim Bibi", "São Paulo", "SP", "", "", "11", "");
        cepDestinoRJ = new CepDTO("22071-000", "Avenida Atlântica", "", "Copacabana", "Rio de Janeiro", "RJ", "", "", "21", "");
    }

    @Test
    @DisplayName("Deve calcular frete com prazo de 1 dia para CEPs com mesmo DDD")
    void calcularTransporte_QuandoMesmoDDD_DeveRetornarPrazoDeUmDia() throws Exception {
        // Arrange
        doReturn(cepOrigemSP).when(service).findCep("01310100");
        doReturn(cepDestinoSP).when(service).findCep("04538132");
        when(repository.save(any(Encomenda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Encomenda resultado = service.calcularTransporte(encomendaDTO);

        assertNotNull(resultado);
        assertEquals(14.5, resultado.getValorFrete()); // 10.0 * 1.45
        assertEquals(LocalDate.now().plusDays(1), resultado.getDataEntrega());
        verify(repository, times(1)).save(any(Encomenda.class));
    }

    @Test
    @DisplayName("Deve calcular frete com prazo de 3 dias para CEPs na mesma UF e DDDs diferentes")
    void calcularTransporte_QuandoMesmaUF_DeveRetornarPrazoDeTresDias() throws Exception {
        // Arrange
        encomendaDTO.setCepDestino("13010002"); // campinas (outro DDD)
        CepDTO cepDestinoCampinas = new CepDTO("13010-002", "Rua...", "", "Centro", "Campinas", "SP", "", "", "19", "");

        doReturn(cepOrigemSP).when(service).findCep("01310100");
        doReturn(cepDestinoCampinas).when(service).findCep("13010002");
        when(repository.save(any(Encomenda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Encomenda resultado = service.calcularTransporte(encomendaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(14.5, resultado.getValorFrete());
        assertEquals(LocalDate.now().plusDays(3), resultado.getDataEntrega());
        verify(repository, times(1)).save(any(Encomenda.class));
    }

    @Test
    @DisplayName("Deve calcular frete com prazo de 10 dias para CEPs em UFs diferentes")
    void calcularTransporte_QuandoUFDiferente_DeveRetornarPrazoDeDezDias() throws Exception {
        // Arrange
        encomendaDTO.setCepDestino("22071000"); // RJ

        doReturn(cepOrigemSP).when(service).findCep("01310100");
        doReturn(cepDestinoRJ).when(service).findCep("22071000");
        when(repository.save(any(Encomenda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Encomenda resultado = service.calcularTransporte(encomendaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(14.5, resultado.getValorFrete());
        assertEquals(LocalDate.now().plusDays(10), resultado.getDataEntrega());
        verify(repository, times(1)).save(any(Encomenda.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CEP de origem ou destino for nulo")
    void calcularTransporte_QuandoCEPNulo_DeveLancarIllegalArgumentException() {
        // Arrange
        encomendaDTO.setCepOrigem(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.calcularTransporte(encomendaDTO);
        }, "CEP de origem e destino são obrigatórios.");
        verify(repository, never()).save(any(Encomenda.class));
    }

    @Test
    @DisplayName("findById deve retornar DTO quando o ID existe")
    void findById_QuandoIdExiste_DeveRetornarEncomendaDTO() {
        // Arrange
        when(repository.findById(existingId)).thenReturn(Optional.of(encomenda));

        // Act
        EncomendaDTO result = service.findById(existingId);

        // Assert
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        verify(repository).findById(existingId);
    }

    @Test
    @DisplayName("findById deve lançar ResourceNotFoundException quando o ID não existe")
    void findById_QuandoIdNaoExiste_DeveLancarResourceNotFoundException() {
        // Arrange
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        verify(repository).findById(nonExistingId);
    }

    @Test
    @DisplayName("delete deve chamar deleteById quando o ID existe")
    void delete_QuandoIdExiste_DeveChamarDeleteById() {
        // Arrange
        when(repository.existsById(existingId)).thenReturn(true);
        doNothing().when(repository).deleteById(existingId);

        // Act & Assert
        assertDoesNotThrow(() -> service.delete(existingId));
        verify(repository).existsById(existingId);
        verify(repository).deleteById(existingId);
    }

    @Test
    @DisplayName("delete deve lançar ResourceNotFoundException quando o ID não existe")
    void delete_QuandoIdNaoExiste_DeveLancarResourceNotFoundException() {
        // Arrange
        when(repository.existsById(nonExistingId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        verify(repository).existsById(nonExistingId);
        verify(repository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Deve alterar o status de ORCADO para PAGO com sucesso")
    void pagarFrete_QuandoStatusForOrcado_DeveMudarStatusParaPago() {
        // Arrange
        Encomenda encomendaOrcada = new Encomenda("Teste", "Dest", "123", "456", 1.0, LocalDate.now(), 10.0);
        encomendaOrcada.setId(existingId);
        
        when(repository.findById(existingId)).thenReturn(Optional.of(encomendaOrcada));
        when(repository.save(any(Encomenda.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EncomendaDTO resultadoDTO = service.pagarFrete(existingId);

        //Assert
        assertNotNull(resultadoDTO);
        assertEquals(StatusEncomenda.PAGO.toString(), resultadoDTO.getStatus());
        verify(repository).save(any(Encomenda.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar encomenda que não está no status ORCADO")
    void pagarFrete_QuandoStatusNaoForOrcado_DeveLancarExcecao() {
        // Arrange
        Encomenda encomendaEnviada = new Encomenda("Teste", "Dest", "123", "456", 1.0, LocalDate.now(), 10.0);
        encomendaEnviada.setId(existingId);
        encomendaEnviada.setStatus(StatusEncomenda.ENVIADO); 

        when(repository.findById(existingId)).thenReturn(Optional.of(encomendaEnviada));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            service.pagarFrete(existingId);
        }, "Apenas encomendas com status ORCADO podem ser pagas.");
    }
}