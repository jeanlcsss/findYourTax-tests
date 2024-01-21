package com.kvsb.findyourtax.dto;

import jakarta.validation.constraints.*;

public class FreteDTO {

    @NotBlank(message = "O nome deve ser preenchido.")
    private String nome;
    @NotBlank(message = "O nome deve ser preenchido.")
    private String nomeDestinatario;
    @NotEmpty(message = "Preencha o cep de origem")
    @Size(min = 8, max = 8)
    private String cepOrigem;
    @NotEmpty(message = "Preencha o cep de destino")
    @Size(min = 8, max = 8)
    private String cepDestino;
    @NotEmpty(message = "O peso deve ser preenchido.")
    private Double peso;

    public FreteDTO(String nome, String nomeDestinatario, String cepOrigem, String cepDestino, Double peso) {
        this.nome = nome;
        this.nomeDestinatario = nomeDestinatario;
        this.cepOrigem = cepOrigem;
        this.cepDestino = cepDestino;
        this.peso = peso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public void setNomeDestinatario(String nomeDestinatario) {
        this.nomeDestinatario = nomeDestinatario;
    }

    public String getCepOrigem() {
        return cepOrigem;
    }

    public void setCepOrigem(String cepOrigem) {
        this.cepOrigem = cepOrigem;
    }

    public String getCepDestino() {
        return cepDestino;
    }

    public void setCepDestino(String cepDestino) {
        this.cepDestino = cepDestino;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
}
