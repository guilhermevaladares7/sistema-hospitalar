package br.pucminas.sih.dto;

import br.pucminas.sih.model.Prioridade;
import jakarta.validation.constraints.NotNull;

public record EmergenciaRequest(
		@NotNull Long pacienteId,
		@NotNull Long profissionalId,
		@NotNull Prioridade prioridade) {
}
