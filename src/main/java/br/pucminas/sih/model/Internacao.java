package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

	@Column(nullable = false)
	private boolean comAcompanhante;

	protected Internacao() {
	}

	public Internacao(Paciente paciente, ProfissionalSaude profissional, Quarto quarto, LocalDate dataEntrada, LocalDate dataPrevistaAlta, boolean comAcompanhante) {
		super(paciente, profissional);
		if (quarto == null) {
			throw new RegraNegocioException("A internação precisa de um quarto.");
		}
		if (dataEntrada == null || dataPrevistaAlta == null) {
			throw new RegraNegocioException("A internação precisa ter data de entrada e data prevista de alta.");
		}
		if (dataPrevistaAlta.isBefore(dataEntrada)) {
			throw new RegraNegocioException("A data prevista de alta não pode ser antes da data de entrada.");
		}
		quarto.validarAcompanhante(comAcompanhante);
		this.quarto = quarto;
		this.dataEntrada = dataEntrada;
		this.dataPrevistaAlta = dataPrevistaAlta;
		this.comAcompanhante = comAcompanhante;
	}

	@Override
	public void validarRealizacao() {
		if (paciente.possuiInternacaoAtiva()) {
			throw new RegraNegocioException("O paciente já possui uma internação ativa.");
		}
		if (!quarto.temVaga()) {
			throw new RegraNegocioException("O quarto " + quarto.getNumero() + " já atingiu a capacidade máxima.");
		}
		quarto.validarAcompanhante(comAcompanhante);
	}

	public long getDiasCobrados() {
		LocalDate fim;
		if (estaAtiva()) {
			fim = dataPrevistaAlta;
		} else {
			fim = dataEfetivaAlta;
		}
		long dias = ChronoUnit.DAYS.between(dataEntrada, fim);
		if (dias < 1) {
			return 1;
		} else {
			return dias;
		}
	}

	public BigDecimal getValorDiaria() {
		return quarto.calcularDiaria(comAcompanhante);
	}

	public BigDecimal getValorTotal() {
		BigDecimal total = getValorDiaria().multiply(BigDecimal.valueOf(getDiasCobrados()));
		return total.setScale(2, RoundingMode.HALF_UP);
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
		if (dataPrevistaAlta == null || dataPrevistaAlta.isBefore(dataEntrada)) {
			throw new RegraNegocioException("A data prevista de alta não pode ser antes da data de entrada.");
		}
		this.dataPrevistaAlta = dataPrevistaAlta;
	}

	public LocalDate getDataEfetivaAlta() {
		return dataEfetivaAlta;
	}

	public boolean isComAcompanhante() {
		return comAcompanhante;
	}

}
