package br.pucminas.sih.dto;

import br.pucminas.sih.model.AtendimentoEmergencia;
import br.pucminas.sih.model.Prioridade;
import br.pucminas.sih.model.SituacaoEmergencia;
import java.time.LocalDateTime;

public record EmergenciaResponse(
		Long id,
		Long pacienteId,
		String pacienteNome,
		Long profissionalId,
		String profissionalNome,
		Prioridade prioridade,
		SituacaoEmergencia situacao,
		LocalDateTime dataHoraChegada,
		String observacoes) {

	public static EmergenciaResponse de(AtendimentoEmergencia atendimento) {
		return new EmergenciaResponse(atendimento.getId(), atendimento.getPaciente().getId(), atendimento.getPaciente().getNome(), atendimento.getProfissional().getId(), atendimento.getProfissional().getNome(), atendimento.getPrioridade(), atendimento.getSituacao(), atendimento.getDataHoraChegada(), atendimento.getObservacoes());
	}

}
