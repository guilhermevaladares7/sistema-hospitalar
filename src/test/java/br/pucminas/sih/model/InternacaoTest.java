package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InternacaoTest {

	private static final LocalDate ENTRADA = LocalDate.of(2026, 10, 14);

	private final Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
	private final Especialista helena = Fixtures.cardiologistaDeQuarta();

	@Test
	void apartamentoComAcompanhanteCobraTresDiarias() {
		Apartamento quarto = Fixtures.apartamento("303", TipoLeito.ELETRICO, true);
		Internacao internacao = new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.plusDays(5), true);
		quarto.ocupar(internacao);

		internacao.darAlta(ENTRADA.plusDays(3));

		assertTrue(internacao.isComAcompanhante());
		assertEquals(3, internacao.getDiasCobrados());
		assertEquals(new BigDecimal("670.00"), internacao.getValorDiaria());
		assertEquals(new BigDecimal("2010.00"), internacao.getValorTotal());
	}

	@Test
	void altaNoMesmoDiaCobraUmaDiaria() {
		Uti quarto = Fixtures.uti("401", 2, true);
		Internacao internacao = new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.plusDays(2), false);
		quarto.ocupar(internacao);

		internacao.darAlta(ENTRADA);

		assertEquals(1, internacao.getDiasCobrados());
		assertEquals(new BigDecimal("1700.00"), internacao.getValorTotal());
	}

	@Test
	void internacaoAtivaEstimaPelaAltaPrevista() {
		Enfermaria quarto = Fixtures.enfermaria("104", 4);
		Internacao internacao = new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.plusDays(7), false);
		quarto.ocupar(internacao);

		assertEquals(7, internacao.getDiasCobrados());
		assertEquals(new BigDecimal("1785.00"), internacao.getValorTotal());

		internacao.setDataPrevistaAlta(ENTRADA.plusDays(10));
		assertEquals(10, internacao.getDiasCobrados());
		assertEquals(new BigDecimal("2550.00"), internacao.getValorTotal());
	}

	@Test
	void altaEfetivaSubstituiAPrevistaNoCalculo() {
		Apartamento quarto = Fixtures.apartamento("301", TipoLeito.PADRAO, false);
		Internacao internacao = new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.plusDays(10), false);
		quarto.ocupar(internacao);

		internacao.darAlta(ENTRADA.plusDays(4));

		assertEquals(4, internacao.getDiasCobrados());
		assertEquals(new BigDecimal("2160.00"), internacao.getValorTotal());
	}

	@Test
	void naoAceitaAcompanhanteEmQuartoQueNaoPermite() {
		assertThrows(RegraNegocioException.class, () -> new Internacao(ana, helena, Fixtures.enfermaria("101", 4), ENTRADA, ENTRADA.plusDays(2), true));
		assertThrows(RegraNegocioException.class, () -> new Internacao(ana, helena, Fixtures.uti("401", 2, false), ENTRADA, ENTRADA.plusDays(2), true));
		assertThrows(RegraNegocioException.class, () -> new Internacao(ana, helena, Fixtures.apartamento("302", TipoLeito.PADRAO, false), ENTRADA, ENTRADA.plusDays(2), true));
	}

	@Test
	void pacienteNaoTemDuasInternacoesAtivas() {
		Enfermaria quarto = Fixtures.enfermaria("104", 4);
		Internacao primeira = new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.plusDays(2), false);
		quarto.ocupar(primeira);
		ana.adicionarInternacao(primeira);
		Internacao segunda = new Internacao(ana, helena, Fixtures.enfermaria("105", 4), ENTRADA, ENTRADA.plusDays(2), false);

		assertThrows(RegraNegocioException.class, segunda::validarRealizacao);
		assertThrows(RegraNegocioException.class, () -> ana.adicionarInternacao(segunda));
	}

	@Test
	void validaDatas() {
		Enfermaria quarto = Fixtures.enfermaria("104", 4);

		assertThrows(RegraNegocioException.class, () -> new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.minusDays(1), false));
		assertThrows(RegraNegocioException.class, () -> new Internacao(ana, helena, quarto, null, ENTRADA, false));
		assertThrows(RegraNegocioException.class, () -> new Internacao(ana, helena, null, ENTRADA, ENTRADA, false));

		Internacao internacao = new Internacao(ana, helena, quarto, ENTRADA, ENTRADA.plusDays(2), false);
		assertThrows(RegraNegocioException.class, () -> internacao.setDataPrevistaAlta(ENTRADA.minusDays(1)));
	}

}
