package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record InicioProcedimentoRequest(
		@NotNull LocalDateTime dataHoraInicio) {
}
