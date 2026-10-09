package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

class QuartoTest {

	private Internacao internar(Paciente paciente, Quarto quarto) {
		ProfissionalSaude medico = Fixtures.cardiologistaDeQuarta();
		Internacao internacao = new Internacao(paciente, medico, quarto, Fixtures.QUARTA, Fixtures.QUARTA.plusDays(5));
		quarto.ocupar(internacao);
		return internacao;
	}

	@Test
	void ficaOcupadoAoAtingirACapacidade() {
		Quarto quarto = new Quarto("103", 1, 2);

		internar(Fixtures.paciente("Ana", "1"), quarto);
		assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
		assertTrue(quarto.temVaga());

		internar(Fixtures.paciente("Carlos", "2"), quarto);
		assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
		assertFalse(quarto.temVaga());
	}

	@Test
	void naoUltrapassaACapacidadeMaxima() {
		Quarto quarto = new Quarto("202", 2, 1);
		internar(Fixtures.paciente("Ana", "1"), quarto);

		assertThrows(RegraNegocioException.class, () -> internar(Fixtures.paciente("Carlos", "2"), quarto));
		assertEquals(1, quarto.getOcupacaoAtual());
	}

	@Test
	void altaLiberaAVaga() {
		Quarto quarto = new Quarto("202", 2, 1);
		Internacao internacao = internar(Fixtures.paciente("Ana", "1"), quarto);

		internacao.darAlta(Fixtures.QUARTA.plusDays(3));

		assertEquals(0, quarto.getOcupacaoAtual());
		assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
		assertEquals(3, internacao.getDiasInternado());
	}

	@Test
	void naoCriaQuartoSemCapacidade() {
		assertThrows(RegraNegocioException.class, () -> new Quarto("999", 1, 0));
	}

}
