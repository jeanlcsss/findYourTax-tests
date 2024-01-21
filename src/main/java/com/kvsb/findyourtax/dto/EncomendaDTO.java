package com.kvsb.findyourtax.dto;


import com.kvsb.findyourtax.entities.Encomenda;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class EncomendaDTO {


    private Long id;
    @NotBlank(message = "Nome necessário.")
    private String nome;
    @NotBlank(message = "Nome do destinatário necessário.")
    private String nomeDestinatario;
    @NotBlank(message = "CEP necessário.")
    @Size(min = 8, max = 8)
    private String cepOrigem;
    @NotBlank(message = "CEP necessário.")
    @Size(min = 8, max = 8)
    private String cepDestino;
    @Positive
    private Double peso;
    private LocalDate dataEntrega;
    @Positive
    private Double valorFrete;


    public EncomendaDTO(Long id, String nome, String nomeDestinatario, String cepOrigem, String cepDestino, Double peso, Double valorFrete, LocalDate dataEntrega) {
        this.id = id;
        this.nome = nome;
        this.nomeDestinatario = nomeDestinatario;
        this.cepOrigem = cepOrigem;
        this.cepDestino = cepDestino;
        this.peso = peso;
        this.valorFrete = valorFrete;
    }

    public EncomendaDTO(Encomenda entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.nomeDestinatario = entity.getNomeDestinatario();
        this.cepOrigem = entity.getCepOrigem();
        this.cepDestino = entity.getCepDestino();
        this.peso = entity.getPeso();
        this.dataEntrega = entity.getDataEntrega();
        this.valorFrete = entity.getValorFrete();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public String getCepOrigem() {
        return cepOrigem;
    }

    public String getCepDestino() {
        return cepDestino;
    }

    public Double getPeso() {
        return peso;
    }

    public LocalDate getDataEntrega() { return dataEntrega; }

    public Double getValorFrete() { return valorFrete; }
}
