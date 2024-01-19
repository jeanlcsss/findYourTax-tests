package com.kvsb.findyourtax.entities;

import com.gtbr.domain.Cep;
import com.kvsb.findyourtax.ViaCepClient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String nomeDestinatario;
    private String cepOrigem;
    private String cepDestino;
    private Double peso;

    public Encomenda(String nome, String nomeDestinatario, String cepOrigem, String cepDestino, Double peso) {
        this.nome = nome;
        this.nomeDestinatario = nomeDestinatario;
        this.cepOrigem = cepOrigem;
        this.cepDestino = cepDestino;
        this.peso = peso;
    }

    public int calculoDiasEntrega(String cepOrigem, String cepDestino) {
        Cep origem = ViaCepClient.findCep(cepOrigem);
        Cep destino = ViaCepClient.findCep(cepDestino);
        String ufOrigem = origem.getUf();
        String ufDestino = destino.getUf();
        String dddOrigem = origem.getDdd();
        String dddDestino = destino.getDdd();

        int diasPrevistos = 0;
        if (dddOrigem.equals(dddDestino)) {
            return diasPrevistos = 1;
        }

        if (ufOrigem.equals(ufDestino)) {
            return diasPrevistos = 3;
        }

        return diasPrevistos = 10;

    }

    public Double valorFrete() {
        double valorFrete = 1.45 * this.getPeso();
        return valorFrete;
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
