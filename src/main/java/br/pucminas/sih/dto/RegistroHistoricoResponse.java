package br.pucminas.sih.dto;

import br.pucminas.sih.model.Atendimento;
import java.time.LocalDate;

public record RegistroHistoricoResponse(
		LocalDate data,
		String resumo,
		String observacoes) {

	public static RegistroHistoricoResponse de(Atendimento atendimento) {
		return new RegistroHistoricoResponse(atendimento.getDataReferencia(), atendimento.getResumo(), atendimento.getObservacoes());
	}

}
