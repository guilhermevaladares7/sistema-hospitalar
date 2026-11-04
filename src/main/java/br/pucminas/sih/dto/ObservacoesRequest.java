package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ObservacoesRequest(
		@NotBlank @Size(max = 2000) String observacoes) {
}
