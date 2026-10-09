package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Disponibilidade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DayOfWeek diaSemana;

	@Column(nullable = false)
	private LocalTime horaInicio;

	@Column(nullable = false)
	private LocalTime horaFim;

	protected Disponibilidade() {
	}

	public Disponibilidade(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
		if (!horaFim.isAfter(horaInicio)) {
			throw new RegraNegocioException("O horário de fim precisa ser depois do horário de início.");
		}
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFim = horaFim;
	}

	public boolean contem(LocalDate data, LocalTime horario) {
		if (data.getDayOfWeek() != diaSemana) {
			return false;
		}
		if (horario.isBefore(horaInicio)) {
			return false;
		}
		if (horario.isBefore(horaFim)) {
			return true;
		} else {
			return false;
		}
	}

	public Long getId() {
		return id;
	}

	public DayOfWeek getDiaSemana() {
		return diaSemana;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFim() {
		return horaFim;
	}

}
