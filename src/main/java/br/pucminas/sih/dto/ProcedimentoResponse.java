package br.pucminas.sih.dto;

import br.pucminas.sih.model.ProcedimentoCirurgico;
import br.pucminas.sih.model.SituacaoProcedimento;
import br.pucminas.sih.model.TipoCirurgia;
import java.time.LocalDateTime;

public record ProcedimentoResponse(
		Long id,
		Long pacienteId,
		String pacienteNome,
		Long cirurgiaoId,
		String cirurgiaoNome,
		String descricao,
		TipoCirurgia tipoCirurgia,
		SituacaoProcedimento situacao,
		LocalDateTime dataHoraPrevista,
		LocalDateTime dataHoraInicio,
		LocalDateTime dataHoraFim,
		String observacoesPreOperatorias,
		String observacoesPosOperatorias) {

	public static ProcedimentoResponse de(ProcedimentoCirurgico procedimento) {
		return new ProcedimentoResponse(procedimento.getId(), procedimento.getPaciente().getId(), procedimento.getPaciente().getNome(), procedimento.getProfissional().getId(), procedimento.getProfissional().getNome(), procedimento.getDescricao(), procedimento.getTipoCirurgia(), procedimento.getSituacao(), procedimento.getDataHoraPrevista(), procedimento.getDataHoraInicio(), procedimento.getDataHoraFim(), procedimento.getObservacoesPreOperatorias(), procedimento.getObservacoesPosOperatorias());
	}

}
