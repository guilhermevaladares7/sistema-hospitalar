package br.pucminas.sih.dto;

import br.pucminas.sih.model.Internacao;
import br.pucminas.sih.model.TipoQuarto;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InternacaoResponse(
		Long id,
		Long pacienteId,
		String pacienteNome,
		Long profissionalId,
		String profissionalNome,
		Long quartoId,
		String quartoNumero,
		TipoQuarto tipoQuarto,
		LocalDate dataEntrada,
		LocalDate dataPrevistaAlta,
		LocalDate dataEfetivaAlta,
		boolean comAcompanhante,
		boolean ativa,
		long diasCobrados,
		BigDecimal valorDiaria,
		BigDecimal valorTotal) {

	public static InternacaoResponse de(Internacao internacao) {
		return new InternacaoResponse(internacao.getId(), internacao.getPaciente().getId(), internacao.getPaciente().getNome(), internacao.getProfissional().getId(), internacao.getProfissional().getNome(), internacao.getQuarto().getId(), internacao.getQuarto().getNumero(), internacao.getQuarto().getTipo(), internacao.getDataEntrada(), internacao.getDataPrevistaAlta(), internacao.getDataEfetivaAlta(), internacao.isComAcompanhante(), internacao.estaAtiva(), internacao.getDiasCobrados(), internacao.getValorDiaria(), internacao.getValorTotal());
	}

}
