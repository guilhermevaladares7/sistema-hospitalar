package br.pucminas.sih.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadeRequest(
		@NotNull DayOfWeek diaSemana,
		@NotNull LocalTime horaInicio,
		@NotNull LocalTime horaFim) {
}
