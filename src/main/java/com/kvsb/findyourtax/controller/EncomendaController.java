package com.kvsb.findyourtax.controller;


import com.kvsb.findyourtax.entities.Encomenda;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kvsb.findyourtax.dto.EncomendaDTO;
import com.kvsb.findyourtax.services.EncomendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/encomendas")
public class EncomendaController {

    private static final Logger log = LoggerFactory.getLogger(EncomendaController.class);

    @Autowired
    private EncomendaService service;

    public EncomendaController(EncomendaService service) {
        this.service = service;
    }

    @PostMapping("/calcular")
    public ResponseEntity<EncomendaDTO> calcularFrete(@Valid @RequestBody EncomendaDTO encomendaDTO) {
        Encomenda resultado = service.calcularTransporte(encomendaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resultado.getId()).toUri();
        return ResponseEntity.created(uri).body(new EncomendaDTO(resultado));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EncomendaDTO> findById(@PathVariable Long id) {
        EncomendaDTO encomendaDTO = service.findById(id);
        return ResponseEntity.ok(encomendaDTO);
    }

    @GetMapping
    public ResponseEntity<Page<EncomendaDTO>> findAll(Pageable pageable) {
        Page<EncomendaDTO> clientDTO = service.findAll(pageable);
        return ResponseEntity.ok(clientDTO);
    }

    @PostMapping
    public ResponseEntity<EncomendaDTO> insert(@Valid @RequestBody EncomendaDTO encomendaDTO) {
        encomendaDTO = service.insert(encomendaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(encomendaDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(encomendaDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<EncomendaDTO> update(@PathVariable Long id, @Valid @RequestBody EncomendaDTO encomendaDTO) {
        encomendaDTO = service.update(id, encomendaDTO);
        return ResponseEntity.ok(encomendaDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/pagar")
    public ResponseEntity<EncomendaDTO> pagarFrete(@PathVariable Long id) {
        EncomendaDTO encomendaDTO = service.pagarFrete(id);
        return ResponseEntity.ok(encomendaDTO);
    }
}
