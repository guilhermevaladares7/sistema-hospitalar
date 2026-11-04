package br.pucminas.sih.dto;

import br.pucminas.sih.model.Especialidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EncaminhamentoRequest(
		@NotNull Especialidade especialidadeDestino,
		@NotBlank @Size(max = 2000) String motivo) {
}
