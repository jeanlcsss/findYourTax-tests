package com.kvsb.findyourtax.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_encomenda")
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String nomeDestinatario;
    private String cepOrigem;
    private String cepDestino;
    private Double peso;
    private LocalDate dataEntrega;
    private Double valorFrete;

    public Encomenda(String nome, String nomeDestinatario, String cepOrigem, String cepDestino, Double peso, LocalDate dataEntrega, Double valorFrete) {
        this.nome = nome;
        this.nomeDestinatario = nomeDestinatario;
        this.cepOrigem = cepOrigem;
        this.cepDestino = cepDestino;
        this.peso = peso;
        this.dataEntrega = dataEntrega;
        this.valorFrete = valorFrete;
    }

    public Encomenda() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(Double valorFrete) {
        this.valorFrete = valorFrete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encomenda encomenda = (Encomenda) o;
        return Objects.equals(id, encomenda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
