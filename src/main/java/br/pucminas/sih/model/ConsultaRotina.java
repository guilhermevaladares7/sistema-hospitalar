package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("ROTINA")
public class ConsultaRotina extends Consulta {

	protected ConsultaRotina() {
	}

	public ConsultaRotina(Paciente paciente, ProfissionalSaude profissional, LocalDate data, LocalTime horario, String motivo) {
		super(paciente, profissional, data, horario, motivo);
	}

	@Override
	public ModalidadeConsulta getModalidade() {
		return ModalidadeConsulta.ROTINA;
	}

	@Override
	public void validarRealizacao() {
		if (!profissional.realizaConsultaDeRotina()) {
			throw new RegraNegocioException("Consulta de rotina só pode ser feita por um clínico geral.");
		}
		validarHorarioNaAgenda();
	}

	@Override
	protected String getDescricaoModalidade() {
		return "Consulta de rotina";
	}

}
