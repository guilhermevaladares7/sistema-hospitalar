package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class CategoriaProfissionalTest {

	@Test
	void somenteClinicoGeralPodeEncaminhar() {
		assertTrue(Fixtures.clinicoDeQuarta().podeEncaminhar());
		assertFalse(Fixtures.cardiologistaDeQuarta().podeEncaminhar());
		assertFalse(Fixtures.cirurgiaoDeQuarta().podeEncaminhar());
	}

	@Test
	void especialistaAtuaSomenteNaSuaEspecialidade() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();

		assertTrue(helena.atuaEm(Especialidade.CARDIOLOGIA));
		assertFalse(helena.atuaEm(Especialidade.NEUROLOGIA));
		assertEquals(Especialidade.CARDIOLOGIA, helena.getEspecialidade());
	}

	@Test
	void clinicoGeralECirurgiaoNaoAtuamEmEspecialidade() {
		assertFalse(Fixtures.clinicoDeQuarta().atuaEm(Especialidade.CARDIOLOGIA));
		assertFalse(Fixtures.cirurgiaoDeQuarta().atuaEm(Especialidade.ORTOPEDIA));
	}

	@Test
	void especialistaPrecisaDeEspecialidade() {
		assertThrows(RegraNegocioException.class, () -> new Especialista("Dra. Camila Fontes", "CRM-MG 40.118", null, "(31) 3290-1004", "camila@sih.com.br"));

		Especialista helena = Fixtures.cardiologistaDeQuarta();
		assertThrows(RegraNegocioException.class, () -> helena.setEspecialidade(null));
		assertEquals(Especialidade.CARDIOLOGIA, helena.getEspecialidade());
	}

	@Test
	void cirurgiaoPrecisaDeAreaCirurgica() {
		assertThrows(RegraNegocioException.class, () -> new Cirurgiao("Dr. Marcos Vinícius Prado", "CRM-MG 51.334", null, "(31) 3290-1003", "marcos@sih.com.br"));

		Cirurgiao marcos = Fixtures.cirurgiaoDeQuarta();
		assertEquals(AreaCirurgica.ORTOPEDICA, marcos.getAreaCirurgica());
		assertThrows(RegraNegocioException.class, () -> marcos.setAreaCirurgica(null));
	}

	@Test
	void somenteClinicoGeralRealizaConsultaDeRotina() {
		assertTrue(Fixtures.clinicoDeQuarta().realizaConsultaDeRotina());
		assertFalse(Fixtures.cardiologistaDeQuarta().realizaConsultaDeRotina());
		assertFalse(Fixtures.cirurgiaoDeQuarta().realizaConsultaDeRotina());
	}

	@Test
	void todasAsCategoriasRespeitamADisponibilidade() {
		ProfissionalSaude[] profissionais = { Fixtures.clinicoDeQuarta(), Fixtures.cardiologistaDeQuarta(), Fixtures.cirurgiaoDeQuarta() };
		LocalTime[] dentro = { LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(15, 0) };
		LocalTime[] fora = { LocalTime.of(12, 0), LocalTime.of(14, 0), LocalTime.of(18, 0) };

		for (int i = 0; i < profissionais.length; i++) {
			assertTrue(profissionais[i].estaDisponivel(Fixtures.QUARTA, dentro[i]));
			assertFalse(profissionais[i].estaDisponivel(Fixtures.QUARTA, fora[i]));
			assertFalse(profissionais[i].estaDisponivel(Fixtures.QUINTA, dentro[i]));
		}
	}

}
