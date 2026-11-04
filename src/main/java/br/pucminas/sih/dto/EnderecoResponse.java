package br.pucminas.sih.dto;

import br.pucminas.sih.model.Endereco;

public record EnderecoResponse(
		String logradouro,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String uf,
		String cep,
		String enderecoCompleto) {

	public static EnderecoResponse de(Endereco endereco) {
		if (endereco == null) {
			return null;
		}
		return new EnderecoResponse(endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(), endereco.getBairro(), endereco.getCidade(), endereco.getUf(), endereco.getCep(), endereco.getEnderecoCompleto());
	}

}
