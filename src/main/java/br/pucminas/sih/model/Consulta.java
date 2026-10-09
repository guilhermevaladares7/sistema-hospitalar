package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Consulta extends Atendimento {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Column(nullable = false)
	private LocalDate data;

	@Column(nullable = false)
	private LocalTime horario;

	@Column(nullable = false)
	private String motivo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusConsulta status;

	protected Consulta() {
	}

	public Consulta(Paciente paciente, ProfissionalSaude profissional, LocalDate data, LocalTime horario, String motivo) {
		super(paciente, profissional);
		this.data = data;
		this.horario = horario;
		this.motivo = motivo;
		this.status = StatusConsulta.AGENDADA;
	}

	public void realizar(String observacoes) {
		if (status != StatusConsulta.AGENDADA) {
			throw new RegraNegocioException("Só é possível realizar uma consulta agendada.");
		}
		registrarObservacao(observacoes);
		status = StatusConsulta.REALIZADA;
	}

	public void cancelar() {
		if (status != StatusConsulta.AGENDADA) {
			throw new RegraNegocioException("Só é possível cancelar uma consulta agendada.");
		}
		status = StatusConsulta.CANCELADA;
	}

	public boolean conflitaCom(LocalDate data, LocalTime horario) {
		if (status == StatusConsulta.CANCELADA) {
			return false;
		}
		if (this.data.equals(data) && this.horario.equals(horario)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public LocalDate getDataReferencia() {
		return data;
	}

	@Override
	public String getResumo() {
		return "Consulta em " + data.format(FORMATO_DATA) + " às " + horario + " com " + profissional.getNome() + " - " + motivo;
	}

	public LocalDate getData() {
		return data;
	}

	public LocalTime getHorario() {
		return horario;
	}

	public String getMotivo() {
		return motivo;
	}

	public StatusConsulta getStatus() {
		return status;
	}

}
