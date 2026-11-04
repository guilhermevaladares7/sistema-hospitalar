package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class QuartoTest {

	private Internacao internar(Paciente paciente, Quarto quarto) {
		ProfissionalSaude medico = Fixtures.cardiologistaDeQuarta();
		Internacao internacao = new Internacao(paciente, medico, quarto, Fixtures.QUARTA, Fixtures.QUARTA.plusDays(5), false);
		quarto.ocupar(internacao);
		return internacao;
	}

	@Test
	void capacidadeDeCadaTipo() {
		assertEquals(6, Fixtures.enfermaria("101", 6).getCapacidadeMaxima());
		assertEquals(1, Fixtures.apartamento("301", TipoLeito.PADRAO, true).getCapacidadeMaxima());
		assertEquals(3, Fixtures.uti("401", 3, false).getCapacidadeMaxima());
	}

	@Test
	void enfermariaFicaOcupadaAoAtingirACapacidade() {
		Enfermaria quarto = Fixtures.enfermaria("103", 2);

		internar(Fixtures.paciente("Ana", "1"), quarto);
		assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
		assertTrue(quarto.temVaga());

		internar(Fixtures.paciente("Carlos", "2"), quarto);
		assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
		assertFalse(quarto.temVaga());
	}

	@Test
	void apartamentoRecebeUmPacienteSo() {
		Apartamento quarto = Fixtures.apartamento("301", TipoLeito.PADRAO, true);
		internar(Fixtures.paciente("Ana", "1"), quarto);

		assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
		assertThrows(RegraNegocioException.class, () -> internar(Fixtures.paciente("Carlos", "2"), quarto));
		assertEquals(1, quarto.getOcupacaoAtual());
	}

	@Test
	void utiNaoUltrapassaACapacidade() {
		Uti quarto = Fixtures.uti("401", 2, false);
		internar(Fixtures.paciente("Ana", "1"), quarto);
		internar(Fixtures.paciente("Carlos", "2"), quarto);

		assertThrows(RegraNegocioException.class, () -> internar(Fixtures.paciente("Fernanda", "3"), quarto));
		assertEquals(2, quarto.getOcupacaoAtual());
	}

	@Test
	void altaLiberaAVaga() {
		Apartamento quarto = Fixtures.apartamento("302", TipoLeito.PADRAO, false);
		Internacao internacao = internar(Fixtures.paciente("Ana", "1"), quarto);

		internacao.darAlta(Fixtures.QUARTA.plusDays(3));

		assertEquals(0, quarto.getOcupacaoAtual());
		assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
		assertEquals(3, internacao.getDiasInternado());
	}

	@Test
	void internacaoNaoPodeSerRealizadaEmQuartoLotado() {
		Apartamento quarto = Fixtures.apartamento("302", TipoLeito.PADRAO, false);
		internar(Fixtures.paciente("Ana", "1"), quarto);
		Internacao outra = new Internacao(Fixtures.paciente("Carlos", "2"), Fixtures.cardiologistaDeQuarta(), quarto, Fixtures.QUARTA, Fixtures.QUINTA, false);

		assertThrows(RegraNegocioException.class, outra::validarRealizacao);
	}

	@Test
	void somenteApartamentoComLeitoDeAcompanhantePermiteAcompanhante() {
		assertTrue(Fixtures.apartamento("301", TipoLeito.PADRAO, true).permiteAcompanhante());
		assertFalse(Fixtures.apartamento("302", TipoLeito.PADRAO, false).permiteAcompanhante());
		assertFalse(Fixtures.enfermaria("101", 4).permiteAcompanhante());
		assertFalse(Fixtures.uti("401", 2, true).permiteAcompanhante());
	}

	@Test
	void diariaComAcompanhanteEmQuartoQueNaoPermiteLancaExcecao() {
		assertThrows(RegraNegocioException.class, () -> Fixtures.enfermaria("101", 4).calcularDiaria(true));
		assertThrows(RegraNegocioException.class, () -> Fixtures.uti("401", 2, false).calcularDiaria(true));
		assertThrows(RegraNegocioException.class, () -> Fixtures.apartamento("302", TipoLeito.PADRAO, false).calcularDiaria(true));
	}

	@Test
	void diariaDaEnfermariaComDescontoPorLeito() {
		assertEquals(new BigDecimal("300.00"), Fixtures.enfermaria("101", 1).calcularDiaria(false));
		assertEquals(new BigDecimal("285.00"), Fixtures.enfermaria("102", 2).calcularDiaria(false));
		assertEquals(new BigDecimal("270.00"), Fixtures.enfermaria("103", 3).calcularDiaria(false));
		assertEquals(new BigDecimal("255.00"), Fixtures.enfermaria("104", 4).calcularDiaria(false));
	}

	@Test
	void descontoDaEnfermariaParaEmVintePorCento() {
		assertEquals(new BigDecimal("240.00"), Fixtures.enfermaria("105", 5).calcularDiaria(false));
		assertEquals(new BigDecimal("240.00"), Fixtures.enfermaria("106", 8).calcularDiaria(false));
		assertEquals(new BigDecimal("0.20"), Fixtures.enfermaria("107", 12).getPercentualDesconto());
	}

	@Test
	void diariaDaEnfermariaArredondaParaDuasCasas() {
		Enfermaria quarto = new Enfermaria("108", 1, new BigDecimal("333.33"), false, false, 3);

		assertEquals(new BigDecimal("300.00"), quarto.calcularDiaria(false));
	}

	@Test
	void diariaDoApartamentoPorTipoDeLeitoEAcompanhante() {
		Apartamento padrao = Fixtures.apartamento("301", TipoLeito.PADRAO, true);
		Apartamento eletrico = Fixtures.apartamento("303", TipoLeito.ELETRICO, true);

		assertEquals(new BigDecimal("540.00"), padrao.calcularDiaria(false));
		assertEquals(new BigDecimal("620.00"), padrao.calcularDiaria(true));
		assertEquals(new BigDecimal("590.00"), eletrico.calcularDiaria(false));
		assertEquals(new BigDecimal("670.00"), eletrico.calcularDiaria(true));
	}

	@Test
	void diariaDaUtiComAcrescimos() {
		assertEquals(new BigDecimal("1500.00"), Fixtures.uti("401", 2, false).calcularDiaria(false));
		assertEquals(new BigDecimal("1700.00"), Fixtures.uti("402", 1, true).calcularDiaria(false));
	}

	@Test
	void diariaDaUtiArredondaParaDuasCasas() {
		Uti quarto = new Uti("403", 4, new BigDecimal("999.99"), true, false, 1, true);

		assertEquals(new BigDecimal("1699.98"), quarto.calcularDiaria(false));
	}

	@Test
	void valorBaseGuardadoComDuasCasas() {
		Enfermaria quarto = new Enfermaria("109", 1, new BigDecimal("250"), false, false, 2);

		assertEquals(new BigDecimal("250.00"), quarto.getValorBaseDiaria());
	}

	@Test
	void validaDadosDoQuarto() {
		assertThrows(RegraNegocioException.class, () -> new Enfermaria("999", 1, new BigDecimal("300.00"), false, false, 0));
		assertThrows(RegraNegocioException.class, () -> new Uti("999", 1, new BigDecimal("300.00"), false, false, 0, false));
		assertThrows(RegraNegocioException.class, () -> new Enfermaria("999", 1, BigDecimal.ZERO, false, false, 2));
		assertThrows(RegraNegocioException.class, () -> new Enfermaria("999", 1, null, false, false, 2));
		assertThrows(RegraNegocioException.class, () -> new Enfermaria(" ", 1, new BigDecimal("300.00"), false, false, 2));
		assertThrows(RegraNegocioException.class, () -> new Apartamento("999", 1, new BigDecimal("300.00"), false, false, null, false));
	}

}
