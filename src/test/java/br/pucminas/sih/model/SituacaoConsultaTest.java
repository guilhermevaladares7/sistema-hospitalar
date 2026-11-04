package br.pucminas.sih.model;

import static br.pucminas.sih.model.SituacaoConsulta.AGENDADA;
import static br.pucminas.sih.model.SituacaoConsulta.CANCELADA;
import static br.pucminas.sih.model.SituacaoConsulta.CONCLUIDA;
import static br.pucminas.sih.model.SituacaoConsulta.CONFIRMADA;
import static br.pucminas.sih.model.SituacaoConsulta.EM_ATENDIMENTO;
import static br.pucminas.sih.model.SituacaoConsulta.NAO_COMPARECEU;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SituacaoConsultaTest {

	private static final Map<SituacaoConsulta, Set<SituacaoConsulta>> PERMITIDAS = Map.of(
			AGENDADA, EnumSet.of(CONFIRMADA, CANCELADA, NAO_COMPARECEU),
			CONFIRMADA, EnumSet.of(EM_ATENDIMENTO, CANCELADA, NAO_COMPARECEU),
			EM_ATENDIMENTO, EnumSet.of(CONCLUIDA),
			CONCLUIDA, EnumSet.noneOf(SituacaoConsulta.class),
			CANCELADA, EnumSet.noneOf(SituacaoConsulta.class),
			NAO_COMPARECEU, EnumSet.noneOf(SituacaoConsulta.class));

	@Test
	void todasAsCombinacoesSeguemATabelaDeTransicoes() {
		for (SituacaoConsulta atual : SituacaoConsulta.values()) {
			for (SituacaoConsulta nova : SituacaoConsulta.values()) {
				boolean esperado = PERMITIDAS.get(atual).contains(nova);
				assertEquals(esperado, atual.podeMudarPara(nova), atual + " -> " + nova);
			}
		}
	}

	@ParameterizedTest
	@CsvSource({
			"AGENDADA, CONFIRMADA",
			"AGENDADA, CANCELADA",
			"AGENDADA, NAO_COMPARECEU",
			"CONFIRMADA, EM_ATENDIMENTO",
			"CONFIRMADA, CANCELADA",
			"CONFIRMADA, NAO_COMPARECEU",
			"EM_ATENDIMENTO, CONCLUIDA"
	})
	void transicoesValidasNaoLancamExcecao(SituacaoConsulta atual, SituacaoConsulta nova) {
		assertDoesNotThrow(() -> atual.validarMudancaPara(nova));
	}

	@ParameterizedTest
	@CsvSource({
			"AGENDADA, AGENDADA",
			"AGENDADA, EM_ATENDIMENTO",
			"AGENDADA, CONCLUIDA",
			"CONFIRMADA, AGENDADA",
			"CONFIRMADA, CONCLUIDA",
			"EM_ATENDIMENTO, CANCELADA",
			"EM_ATENDIMENTO, NAO_COMPARECEU",
			"EM_ATENDIMENTO, AGENDADA",
			"CONCLUIDA, AGENDADA",
			"CONCLUIDA, CANCELADA",
			"CANCELADA, CONCLUIDA",
			"CANCELADA, AGENDADA",
			"NAO_COMPARECEU, CONFIRMADA",
			"NAO_COMPARECEU, AGENDADA"
	})
	void transicoesInvalidasLancamRegraNegocioException(SituacaoConsulta atual, SituacaoConsulta nova) {
		assertThrows(RegraNegocioException.class, () -> atual.validarMudancaPara(nova));
	}

	@Test
	void concluidaCanceladaENaoCompareceuSaoFinais() {
		assertTrue(CONCLUIDA.ehFinal());
		assertTrue(CANCELADA.ehFinal());
		assertTrue(NAO_COMPARECEU.ehFinal());
		assertFalse(AGENDADA.ehFinal());
		assertFalse(CONFIRMADA.ehFinal());
		assertFalse(EM_ATENDIMENTO.ehFinal());
	}

	@Test
	void somenteCanceladaENaoCompareceuLiberamOHorario() {
		assertFalse(CANCELADA.ocupaHorario());
		assertFalse(NAO_COMPARECEU.ocupaHorario());
		assertTrue(AGENDADA.ocupaHorario());
		assertTrue(CONFIRMADA.ocupaHorario());
		assertTrue(EM_ATENDIMENTO.ocupaHorario());
		assertTrue(CONCLUIDA.ocupaHorario());
	}

}
