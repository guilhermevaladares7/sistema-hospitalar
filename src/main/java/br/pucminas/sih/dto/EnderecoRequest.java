package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequest(
		@NotBlank String logradouro,
		@NotBlank String numero,
		String complemento,
		@NotBlank String bairro,
		@NotBlank String cidade,
		@NotBlank @Pattern(regexp = "[A-Z]{2}", message = "deve ter a sigla da UF com 2 letras maiúsculas") String uf,
		@NotBlank @Pattern(regexp = "\\d{5}-\\d{3}", message = "deve estar no formato 00000-000") String cep) {
}
