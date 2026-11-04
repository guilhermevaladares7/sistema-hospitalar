package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "modalidade", discriminatorType = DiscriminatorType.STRING)
public abstract class Consulta extends Atendimento {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Column(nullable = false)
	private LocalDate data;

	@Column(nullable = false)
	private LocalTime horario;

	@Column(nullable = false)
	private String motivo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SituacaoConsulta situacao;

	protected Consulta() {
	}

	protected Consulta(Paciente paciente, ProfissionalSaude profissional, LocalDate data, LocalTime horario, String motivo) {
		super(paciente, profissional);
		if (data == null || horario == null) {
			throw new RegraNegocioException("A consulta precisa ter data e horário.");
		}
		if (motivo == null || motivo.isBlank()) {
			throw new RegraNegocioException("A consulta precisa ter um motivo.");
		}
		this.data = data;
		this.horario = horario;
		this.motivo = motivo;
		this.situacao = SituacaoConsulta.AGENDADA;
	}

	protected abstract String getDescricaoModalidade();

	protected void validarHorarioNaAgenda() {
		if (!profissional.atendeNoHorario(data, horario)) {
			throw new RegraNegocioException("O profissional não atende nesse dia e horário.");
		}
		if (profissional.possuiOutraConsultaNoHorario(this)) {
			throw new RegraNegocioException("O profissional já tem uma consulta nesse dia e horário.");
		}
	}

	public void confirmar() {
		mudarSituacao(SituacaoConsulta.CONFIRMADA);
	}

	public void iniciar() {
		situacao.validarMudancaPara(SituacaoConsulta.EM_ATENDIMENTO);
		validarRealizacao();
		situacao = SituacaoConsulta.EM_ATENDIMENTO;
	}

	public void concluir(String observacoes) {
		situacao.validarMudancaPara(SituacaoConsulta.CONCLUIDA);
		registrarObservacao(observacoes);
		situacao = SituacaoConsulta.CONCLUIDA;
	}

	public void cancelar() {
		mudarSituacao(SituacaoConsulta.CANCELADA);
	}

	public void registrarNaoComparecimento() {
		mudarSituacao(SituacaoConsulta.NAO_COMPARECEU);
	}

	private void mudarSituacao(SituacaoConsulta nova) {
		situacao.validarMudancaPara(nova);
		situacao = nova;
	}

	public boolean conflitaCom(LocalDate data, LocalTime horario) {
		if (!situacao.ocupaHorario()) {
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
		return getDescricaoModalidade() + " em " + data.format(FORMATO_DATA) + " às " + horario + " com " + profissional.getNome() + " - " + motivo;
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

	public SituacaoConsulta getSituacao() {
		return situacao;
	}

}
