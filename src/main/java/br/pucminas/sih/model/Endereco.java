package br.pucminas.sih.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Endereco {

	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String uf;
	private String cep;

	protected Endereco() {
	}

	public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade, String uf, String cep) {
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.bairro = bairro;
		this.cidade = cidade;
		this.uf = uf;
		this.cep = cep;
	}

	public String getEnderecoCompleto() {
		StringBuilder texto = new StringBuilder();
		texto.append(logradouro).append(", ").append(numero);
		if (complemento != null && !complemento.isBlank()) {
			texto.append(", ").append(complemento);
		}
		texto.append(" - ").append(bairro);
		texto.append(", ").append(cidade).append(" - ").append(uf);
		texto.append(", CEP ").append(cep);
		return texto.toString();
	}

	public String getLogradouro() {
		return logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public String getUf() {
		return uf;
	}

	public String getCep() {
		return cep;
	}

}
