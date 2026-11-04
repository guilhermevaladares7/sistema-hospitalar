package br.pucminas.sih.dto;

import br.pucminas.sih.model.Especialidade;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaEspecializadaRequest(
		@NotNull Long pacienteId,
		@NotNull Long profissionalId,
		@NotNull Especialidade especialidade,
		@NotNull @FutureOrPresent LocalDate data,
		@NotNull LocalTime horario,
		@NotBlank String motivo) {
}
