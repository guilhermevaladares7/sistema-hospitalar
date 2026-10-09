package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
public class Internacao extends Atendimento {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@ManyToOne(optional = false)
	@JoinColumn(name = "quarto_id")
	private Quarto quarto;

	@Column(nullable = false)
	private LocalDate dataEntrada;

	@Column(nullable = false)
	private LocalDate dataPrevistaAlta;

	private LocalDate dataEfetivaAlta;

	protected Internacao() {
	}

	public Internacao(Paciente paciente, ProfissionalSaude profissional, Quarto quarto, LocalDate dataEntrada, LocalDate dataPrevistaAlta) {
		super(paciente, profissional);
		if (dataPrevistaAlta.isBefore(dataEntrada)) {
			throw new RegraNegocioException("A data prevista de alta não pode ser antes da data de entrada.");
		}
		this.quarto = quarto;
		this.dataEntrada = dataEntrada;
		this.dataPrevistaAlta = dataPrevistaAlta;
	}

	public boolean estaAtiva() {
		if (dataEfetivaAlta == null) {
			return true;
		} else {
			return false;
		}
	}

	public void darAlta(LocalDate data) {
		if (!estaAtiva()) {
			throw new RegraNegocioException("Essa internação já foi finalizada.");
		}
		if (data.isBefore(dataEntrada)) {
			throw new RegraNegocioException("A data de alta não pode ser antes da data de entrada.");
		}
		dataEfetivaAlta = data;
		quarto.liberar(this);
	}

	public long getDiasInternado() {
		LocalDate fim;
		if (estaAtiva()) {
			fim = LocalDate.now();
		} else {
			fim = dataEfetivaAlta;
		}
		return ChronoUnit.DAYS.between(dataEntrada, fim);
	}

	@Override
	public LocalDate getDataReferencia() {
		return dataEntrada;
	}

	@Override
	public String getResumo() {
		String periodo;
		if (estaAtiva()) {
			periodo = "desde " + dataEntrada.format(FORMATO_DATA);
		} else {
			periodo = "de " + dataEntrada.format(FORMATO_DATA) + " a " + dataEfetivaAlta.format(FORMATO_DATA);
		}
		return "Internação no quarto " + quarto.getNumero() + " " + periodo + " com " + profissional.getNome();
	}

	public Quarto getQuarto() {
		return quarto;
	}

	public LocalDate getDataEntrada() {
		return dataEntrada;
	}

	public LocalDate getDataPrevistaAlta() {
		return dataPrevistaAlta;
	}

	public void setDataPrevistaAlta(LocalDate dataPrevistaAlta) {
		this.dataPrevistaAlta = dataPrevistaAlta;
	}

	public LocalDate getDataEfetivaAlta() {
		return dataEfetivaAlta;
	}

}
