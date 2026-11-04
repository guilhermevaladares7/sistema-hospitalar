package br.pucminas.sih.dto;

import br.pucminas.sih.model.Encaminhamento;
import br.pucminas.sih.model.Especialidade;
import java.time.LocalDate;

public record EncaminhamentoResponse(
		Long id,
		Long pacienteId,
		String pacienteNome,
		Long clinicoId,
		String clinicoNome,
		Long consultaOrigemId,
		Especialidade especialidadeDestino,
		String motivo,
		LocalDate data) {

	public static EncaminhamentoResponse de(Encaminhamento encaminhamento) {
		return new EncaminhamentoResponse(encaminhamento.getId(), encaminhamento.getPaciente().getId(), encaminhamento.getPaciente().getNome(), encaminhamento.getClinico().getId(), encaminhamento.getClinico().getNome(), encaminhamento.getConsultaOrigem().getId(), encaminhamento.getEspecialidadeDestino(), encaminhamento.getMotivo(), encaminhamento.getData());
	}

}
