package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ConclusaoProcedimentoRequest(
		@NotNull LocalDateTime dataHoraFim,
		@Size(max = 2000) String observacoesPosOperatorias) {
}
