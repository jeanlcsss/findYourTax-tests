package com.kvsb.findyourtax;

import com.kvsb.findyourtax.entities.Cep;
import com.kvsb.findyourtax.entities.Encomenda;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindYourTaxApplication {

	public static void main(String[] args) {

		Encomenda encomenda = new Encomenda("Notebook", "Kaio", "53413310", "50870900", 10.5);
		System.out.println("Dias para entrega: " + encomenda.calculoDiasEntrega(encomenda.getCepOrigem(), encomenda.getCepDestino()));
		System.out.println("Frete: " + encomenda.valorFrete());

		SpringApplication.run(FindYourTaxApplication.class, args);
	}

}
