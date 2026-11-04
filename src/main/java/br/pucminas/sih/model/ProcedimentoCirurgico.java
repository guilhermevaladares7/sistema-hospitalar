package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class ProcedimentoCirurgico extends Atendimento {

	private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Column(nullable = false)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoCirurgia tipoCirurgia;

	@Column(length = 2000)
	private String observacoesPreOperatorias;

	@Column(length = 2000)
	private String observacoesPosOperatorias;

	@Column(nullable = false)
	private LocalDateTime dataHoraPrevista;

	private LocalDateTime dataHoraInicio;

	private LocalDateTime dataHoraFim;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SituacaoProcedimento situacao;

	protected ProcedimentoCirurgico() {
	}

	public ProcedimentoCirurgico(Paciente paciente, ProfissionalSaude profissional, String descricao, TipoCirurgia tipoCirurgia, LocalDateTime dataHoraPrevista, String observacoesPreOperatorias) {
		super(paciente, profissional);
		if (descricao == null || descricao.isBlank()) {
			throw new RegraNegocioException("O procedimento precisa ter uma descrição.");
		}
		if (tipoCirurgia == null) {
			throw new RegraNegocioException("O procedimento precisa ter um tipo de cirurgia.");
		}
		if (dataHoraPrevista == null) {
			throw new RegraNegocioException("O procedimento precisa ter data e hora previstas.");
		}
		this.descricao = descricao;
		this.tipoCirurgia = tipoCirurgia;
		this.dataHoraPrevista = dataHoraPrevista;
		this.observacoesPreOperatorias = observacoesPreOperatorias;
		this.situacao = SituacaoProcedimento.AGENDADO;
	}

	@Override
	public void validarRealizacao() {
		if (!profissional.realizaProcedimentoCirurgico()) {
			throw new RegraNegocioException("Procedimento cirúrgico só pode ser feito por um cirurgião.");
		}
		if (profissional.possuiOutroProcedimentoNoHorario(this)) {
			throw new RegraNegocioException("O cirurgião já tem um procedimento previsto para esse dia e horário.");
		}
	}

	public boolean conflitaCom(LocalDateTime dataHora) {
		if (!situacao.ocupaHorario()) {
			return false;
		}
		if (dataHoraPrevista.equals(dataHora)) {
			return true;
		} else {
			return false;
		}
	}

	public void registrarObservacoesPreOperatorias(String observacoes) {
		if (situacao != SituacaoProcedimento.AGENDADO) {
			throw new RegraNegocioException("As observações pré-operatórias só podem ser registradas antes de iniciar o procedimento.");
		}
		observacoesPreOperatorias = observacoes;
	}

	public void iniciar(LocalDateTime inicio) {
		situacao.validarMudancaPara(SituacaoProcedimento.EM_ANDAMENTO);
		if (inicio == null) {
			throw new RegraNegocioException("Informe a data e hora de início do procedimento.");
		}
		validarRealizacao();
		dataHoraInicio = inicio;
		situacao = SituacaoProcedimento.EM_ANDAMENTO;
	}

	public void concluir(LocalDateTime fim, String observacoesPosOperatorias) {
		situacao.validarMudancaPara(SituacaoProcedimento.CONCLUIDO);
		if (fim == null) {
			throw new RegraNegocioException("Informe a data e hora de término do procedimento.");
		}
		if (fim.isBefore(dataHoraInicio)) {
			throw new RegraNegocioException("O término do procedimento não pode ser antes do início.");
		}
		dataHoraFim = fim;
		this.observacoesPosOperatorias = observacoesPosOperatorias;
		situacao = SituacaoProcedimento.CONCLUIDO;
	}

	public void cancelar() {
		situacao.validarMudancaPara(SituacaoProcedimento.CANCELADO);
		situacao = SituacaoProcedimento.CANCELADO;
	}

	public boolean foiRealizado() {
		if (situacao == SituacaoProcedimento.CONCLUIDO) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public LocalDate getDataReferencia() {
		return dataHoraPrevista.toLocalDate();
	}

	@Override
	public String getResumo() {
		return "Procedimento cirúrgico (" + tipoCirurgia + ") previsto para " + dataHoraPrevista.format(FORMATO_DATA_HORA) + " com " + profissional.getNome() + " - " + descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public TipoCirurgia getTipoCirurgia() {
		return tipoCirurgia;
	}

	public String getObservacoesPreOperatorias() {
		return observacoesPreOperatorias;
	}

	public String getObservacoesPosOperatorias() {
		return observacoesPosOperatorias;
	}

	public LocalDateTime getDataHoraPrevista() {
		return dataHoraPrevista;
	}

	public LocalDateTime getDataHoraInicio() {
		return dataHoraInicio;
	}

	public LocalDateTime getDataHoraFim() {
		return dataHoraFim;
	}

	public SituacaoProcedimento getSituacao() {
		return situacao;
	}

}
