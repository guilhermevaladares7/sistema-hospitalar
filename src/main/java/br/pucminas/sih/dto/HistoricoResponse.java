package br.pucminas.sih.dto;

import java.util.List;

public record HistoricoResponse(
		PacienteResponse paciente,
		List<RegistroHistoricoResponse> atendimentos,
		List<EncaminhamentoResponse> encaminhamentos) {
}
