package com.kvsb.findyourtax.services;

import com.google.gson.Gson;
import com.kvsb.findyourtax.dto.CepDTO;
import com.kvsb.findyourtax.dto.EncomendaDTO;
import com.kvsb.findyourtax.dto.FreteDTO;
import com.kvsb.findyourtax.entities.Encomenda;
import com.kvsb.findyourtax.repositories.EncomendaRepository;
import com.kvsb.findyourtax.services.exceptions.ResourceNotFoundException;
import com.kvsb.findyourtax.utils.CepUtils;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class EncomendaService {

    private static final Logger log = LoggerFactory.getLogger(EncomendaService.class);

    private static final String viaCepUrl = "https://viacep.com.br/ws/";
    private final Gson gson;
    private final EncomendaRepository repository;

    public EncomendaService(Gson gson, EncomendaRepository repository){
        this.gson = gson;
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public EncomendaDTO findById(Long id) {
        Encomenda encomenda = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Id não encontrado."));
        return new EncomendaDTO(encomenda);
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> findAll(Pageable pageable) {
        Page<Encomenda> list = repository.findAll(pageable);
        return list.map(EncomendaDTO::new);
    }

    @Transactional
    public EncomendaDTO insert(EncomendaDTO encomendaDTO) {

        Encomenda entity = new Encomenda();
        copyDtoToEntity(encomendaDTO, entity);
        entity = repository.save(entity);
        return new EncomendaDTO(entity);

    }

    @Transactional
    public EncomendaDTO update(Long id, EncomendaDTO encomendaDTO) {
        try {
            Encomenda entity = repository.getReferenceById(id);
            copyDtoToEntity(encomendaDTO, entity);
            entity = repository.save(entity);
            return new EncomendaDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado.");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Id não encontrado.");
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error at referencial integrity");
        }

    }

    public Encomenda calcularTransporte(EncomendaDTO encomendaDTO) {
        LocalDate dataPrevista = LocalDate.now();

        if (encomendaDTO.getCepOrigem() == null || encomendaDTO.getCepDestino() == null) {
            throw new IllegalArgumentException("CEP de origem e destino são obrigatórios.");
        }

        CepDTO cepOrigem = findCep(encomendaDTO.getCepOrigem());
        CepDTO cepDestino = findCep(encomendaDTO.getCepDestino());

        Double valorFrete = encomendaDTO.getPeso() * 1.45;

        if (cepOrigem.getDdd() != null && cepDestino.getDdd() != null && cepOrigem.getDdd().equals(cepDestino.getDdd())) {
            dataPrevista = LocalDate.now().plusDays(1);
        } else if (cepOrigem.getUf() != null && cepDestino.getUf() != null && cepOrigem.getUf().equals(cepDestino.getUf())) {
            dataPrevista = LocalDate.now().plusDays(3);
        } else {
            dataPrevista = LocalDate.now().plusDays(10);
        }



        FreteDTO freteDTO = new FreteDTO(
                encomendaDTO.getNome(),
                encomendaDTO.getNomeDestinatario(),
                encomendaDTO.getCepOrigem(),
                encomendaDTO.getCepDestino(),
                encomendaDTO.getPeso());

        Encomenda resultado = new Encomenda(
                "Kaio", freteDTO.getNomeDestinatario(), cepOrigem.getCep(), cepDestino.getCep(), freteDTO.getPeso(), dataPrevista, valorFrete
        );

        repository.save(resultado);

        return resultado;
    }

    public CepDTO findCep(String cep) {
        CepUtils.validaCep(cep);
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.of(1, MINUTES))
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(viaCepUrl+cep+"/json"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("[VIA CEP API] - [RESULTADO DA BUSCA: {}]", httpResponse.body());

            return gson.fromJson(httpResponse.body(), CepDTO.class);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void copyDtoToEntity(EncomendaDTO encomendaDTO, Encomenda entity) {
        entity.setNome(encomendaDTO.getNome());
        entity.setNomeDestinatario(encomendaDTO.getNomeDestinatario());
        entity.setCepOrigem(encomendaDTO.getCepOrigem());
        entity.setCepDestino(encomendaDTO.getCepDestino());
        entity.setPeso(encomendaDTO.getPeso());
        entity.setDataEntrega(encomendaDTO.getDataEntrega());
        entity.setValorFrete(encomendaDTO.getValorFrete());
    }

}
