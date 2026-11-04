package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Entity
public class AtendimentoEmergencia extends Atendimento {

	public static final Comparator<AtendimentoEmergencia> ORDEM_DE_ATENDIMENTO = AtendimentoEmergencia::compararParaAtendimento;

	private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Prioridade prioridade;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SituacaoEmergencia situacao;

	@Column(nullable = false)
	private LocalDateTime dataHoraChegada;

	protected AtendimentoEmergencia() {
	}

	public AtendimentoEmergencia(Paciente paciente, ProfissionalSaude profissional, Prioridade prioridade, LocalDateTime dataHoraChegada) {
		super(paciente, profissional);
		if (prioridade == null) {
			throw new RegraNegocioException("O atendimento de emergência precisa ter uma prioridade.");
		}
		if (dataHoraChegada == null) {
			throw new RegraNegocioException("O atendimento de emergência precisa ter a data e hora de chegada.");
		}
		this.prioridade = prioridade;
		this.dataHoraChegada = dataHoraChegada;
		this.situacao = SituacaoEmergencia.AGUARDANDO_AVALIACAO;
	}

	public static int compararParaAtendimento(AtendimentoEmergencia primeiro, AtendimentoEmergencia segundo) {
		if (primeiro.prioridade.ehMaisUrgenteQue(segundo.prioridade)) {
			return -1;
		} else if (segundo.prioridade.ehMaisUrgenteQue(primeiro.prioridade)) {
			return 1;
		} else {
			return primeiro.dataHoraChegada.compareTo(segundo.dataHoraChegada);
		}
	}

	@Override
	public void validarRealizacao() {
		situacao.validarMudancaPara(SituacaoEmergencia.EM_ATENDIMENTO);
	}

	public void iniciar() {
		validarRealizacao();
		situacao = SituacaoEmergencia.EM_ATENDIMENTO;
	}

	public void concluir(String observacoes) {
		situacao.validarMudancaPara(SituacaoEmergencia.CONCLUIDO);
		registrarObservacao(observacoes);
		situacao = SituacaoEmergencia.CONCLUIDO;
	}

	public boolean aguardaAvaliacao() {
		if (situacao == SituacaoEmergencia.AGUARDANDO_AVALIACAO) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public LocalDate getDataReferencia() {
		return dataHoraChegada.toLocalDate();
	}

	@Override
	public String getResumo() {
		return "Emergência de prioridade " + prioridade + " em " + dataHoraChegada.format(FORMATO_DATA_HORA) + " com " + profissional.getNome();
	}

	public Prioridade getPrioridade() {
		return prioridade;
	}

	public SituacaoEmergencia getSituacao() {
		return situacao;
	}

	public LocalDateTime getDataHoraChegada() {
		return dataHoraChegada;
	}

}
