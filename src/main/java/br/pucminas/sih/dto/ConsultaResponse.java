package br.pucminas.sih.dto;

import br.pucminas.sih.model.Consulta;
import br.pucminas.sih.model.ConsultaEspecializada;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.ModalidadeConsulta;
import br.pucminas.sih.model.SituacaoConsulta;
import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaResponse(
		Long id,
		ModalidadeConsulta modalidade,
		Long pacienteId,
		String pacienteNome,
		Long profissionalId,
		String profissionalNome,
		Especialidade especialidade,
		LocalDate data,
		LocalTime horario,
		String motivo,
		SituacaoConsulta situacao,
		String observacoes) {

	public static ConsultaResponse de(Consulta consulta) {
		Especialidade especialidade = null;
		if (consulta instanceof ConsultaEspecializada especializada) {
			especialidade = especializada.getEspecialidade();
		}
		return new ConsultaResponse(consulta.getId(), consulta.getModalidade(), consulta.getPaciente().getId(), consulta.getPaciente().getNome(), consulta.getProfissional().getId(), consulta.getProfissional().getNome(), especialidade, consulta.getData(), consulta.getHorario(), consulta.getMotivo(), consulta.getSituacao(), consulta.getObservacoes());
	}

}
