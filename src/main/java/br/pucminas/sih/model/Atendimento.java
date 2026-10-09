package br.pucminas.sih.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
public abstract class Atendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "paciente_id")
	protected Paciente paciente;

	@ManyToOne(optional = false)
	@JoinColumn(name = "profissional_id")
	protected ProfissionalSaude profissional;

	@Column(length = 2000)
	protected String observacoes;

	protected Atendimento() {
	}

	protected Atendimento(Paciente paciente, ProfissionalSaude profissional) {
		this.paciente = paciente;
		this.profissional = profissional;
	}

	public void registrarObservacao(String texto) {
		if (observacoes == null || observacoes.isBlank()) {
			observacoes = texto;
		} else {
			observacoes = observacoes + "\n" + texto;
		}
	}

	public abstract LocalDate getDataReferencia();

	public abstract String getResumo();

	public Long getId() {
		return id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public ProfissionalSaude getProfissional() {
		return profissional;
	}

	public String getObservacoes() {
		return observacoes;
	}

}
