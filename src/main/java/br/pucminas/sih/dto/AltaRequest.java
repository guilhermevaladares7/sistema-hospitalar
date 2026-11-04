package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AltaRequest(
		@NotNull LocalDate dataAlta) {
}
