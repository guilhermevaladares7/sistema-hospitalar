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
	void todasAsCategoriasRespeitamDisponibilidadeEConflitoDeHorario() {
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Paciente carlos = Fixtures.paciente("Carlos Eduardo Lima", "234.567.890-11");
		ProfissionalSaude[] profissionais = { Fixtures.clinicoDeQuarta(), Fixtures.cardiologistaDeQuarta(), Fixtures.cirurgiaoDeQuarta() };
		LocalTime[] horarios = { LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(15, 0) };

		for (int i = 0; i < profissionais.length; i++) {
			ProfissionalSaude profissional = profissionais[i];
			LocalTime horario = horarios[i];

			profissional.adicionarConsulta(new Consulta(ana, profissional, Fixtures.QUARTA, horario, "Avaliação"));
			Consulta mesmoHorario = new Consulta(carlos, profissional, Fixtures.QUARTA, horario, "Avaliação");
			Consulta foraDaAgenda = new Consulta(carlos, profissional, Fixtures.QUINTA, horario, "Avaliação");

			assertFalse(profissional.estaDisponivel(Fixtures.QUARTA, horario));
			assertThrows(RegraNegocioException.class, () -> profissional.adicionarConsulta(mesmoHorario));
			assertThrows(RegraNegocioException.class, () -> profissional.adicionarConsulta(foraDaAgenda));
			assertEquals(1, profissional.getConsultas().size());
		}
	}

}
