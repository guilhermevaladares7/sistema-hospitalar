package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("ESPECIALIZADA")
public class ConsultaEspecializada extends Consulta {

	@Enumerated(EnumType.STRING)
	@Column(name = "especialidade")
	private Especialidade especialidade;

	protected ConsultaEspecializada() {
	}

	public ConsultaEspecializada(Paciente paciente, ProfissionalSaude profissional, Especialidade especialidade, LocalDate data, LocalTime horario, String motivo) {
		super(paciente, profissional, data, horario, motivo);
		if (especialidade == null) {
			throw new RegraNegocioException("A consulta especializada precisa ter uma especialidade.");
		}
		this.especialidade = especialidade;
	}

	@Override
	public ModalidadeConsulta getModalidade() {
		return ModalidadeConsulta.ESPECIALIZADA;
	}

	@Override
	public void validarRealizacao() {
		if (!profissional.atuaEm(especialidade)) {
			throw new RegraNegocioException("O profissional " + profissional.getNome() + " não é especialista em " + especialidade + ".");
		}
		validarHorarioNaAgenda();
	}

	@Override
	protected String getDescricaoModalidade() {
		return "Consulta especializada (" + especialidade + ")";
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

}
