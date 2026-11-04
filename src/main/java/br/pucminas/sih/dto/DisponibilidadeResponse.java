package br.pucminas.sih.dto;

import br.pucminas.sih.model.Disponibilidade;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadeResponse(
		DayOfWeek diaSemana,
		LocalTime horaInicio,
		LocalTime horaFim) {

	public static DisponibilidadeResponse de(Disponibilidade disponibilidade) {
		return new DisponibilidadeResponse(disponibilidade.getDiaSemana(), disponibilidade.getHoraInicio(), disponibilidade.getHoraFim());
	}

}
