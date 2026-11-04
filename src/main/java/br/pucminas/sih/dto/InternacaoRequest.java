package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record InternacaoRequest(
		@NotNull Long pacienteId,
		@NotNull Long profissionalId,
		@NotNull Long quartoId,
		@NotNull LocalDate dataEntrada,
		@NotNull LocalDate dataPrevistaAlta,
		boolean comAcompanhante) {
}
