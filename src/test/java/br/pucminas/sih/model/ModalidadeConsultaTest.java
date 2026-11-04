package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class ModalidadeConsultaTest {

	private final Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
	private final Paciente carlos = Fixtures.paciente("Carlos Eduardo Lima", "234.567.890-11");

	@Test
	void consultaDeRotinaComClinicoGeral() {
		ClinicoGeral rafael = Fixtures.clinicoDeQuarta();
		ConsultaRotina consulta = Fixtures.rotina(ana, rafael, Fixtures.QUARTA, LocalTime.of(9, 0));

		assertDoesNotThrow(() -> rafael.adicionarConsulta(consulta));
		assertEquals(1, rafael.getConsultas().size());
		assertTrue(consulta.getResumo().startsWith("Consulta de rotina"));
	}

	@Test
	void consultaDeRotinaNaoPodeSerComEspecialistaNemCirurgiao() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Cirurgiao marcos = Fixtures.cirurgiaoDeQuarta();
		ConsultaRotina comEspecialista = Fixtures.rotina(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0));
		ConsultaRotina comCirurgiao = Fixtures.rotina(ana, marcos, Fixtures.QUARTA, LocalTime.of(15, 0));

		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(comEspecialista));
		assertThrows(RegraNegocioException.class, () -> marcos.adicionarConsulta(comCirurgiao));
		assertEquals(0, helena.getConsultas().size());
		assertEquals(0, marcos.getConsultas().size());
	}

	@Test
	void consultaDeRotinaRespeitaDisponibilidadeEConflito() {
		ClinicoGeral rafael = Fixtures.clinicoDeQuarta();
		rafael.adicionarConsulta(Fixtures.rotina(ana, rafael, Fixtures.QUARTA, LocalTime.of(9, 0)));
		ConsultaRotina mesmoHorario = Fixtures.rotina(carlos, rafael, Fixtures.QUARTA, LocalTime.of(9, 0));
		ConsultaRotina depoisDoExpediente = Fixtures.rotina(carlos, rafael, Fixtures.QUARTA, LocalTime.of(12, 0));
		ConsultaRotina outroDia = Fixtures.rotina(carlos, rafael, Fixtures.QUINTA, LocalTime.of(9, 0));

		assertThrows(RegraNegocioException.class, () -> rafael.adicionarConsulta(mesmoHorario));
		assertThrows(RegraNegocioException.class, () -> rafael.adicionarConsulta(depoisDoExpediente));
		assertThrows(RegraNegocioException.class, () -> rafael.adicionarConsulta(outroDia));
		assertEquals(1, rafael.getConsultas().size());
	}

	@Test
	void consultaEspecializadaComEspecialistaDaMesmaArea() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		ConsultaEspecializada consulta = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(10, 0));

		assertDoesNotThrow(() -> helena.adicionarConsulta(consulta));
		assertEquals(Especialidade.CARDIOLOGIA, consulta.getEspecialidade());
		assertTrue(consulta.getResumo().startsWith("Consulta especializada (CARDIOLOGIA)"));
	}

	@Test
	void consultaEspecializadaComEspecialidadeIncompativel() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		ConsultaEspecializada neurologia = new ConsultaEspecializada(ana, helena, Especialidade.NEUROLOGIA, Fixtures.QUARTA, LocalTime.of(10, 0), "Enxaqueca");

		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(neurologia));
		assertEquals(0, helena.getConsultas().size());
	}

	@Test
	void clinicoGeralNaoFazConsultaEspecializada() {
		ClinicoGeral rafael = Fixtures.clinicoDeQuarta();
		ConsultaEspecializada consulta = Fixtures.cardiologia(ana, rafael, Fixtures.QUARTA, LocalTime.of(9, 0));

		assertThrows(RegraNegocioException.class, () -> rafael.adicionarConsulta(consulta));
		assertEquals(0, rafael.getConsultas().size());
	}

	@Test
	void cirurgiaoNaoFazConsultaEspecializada() {
		Cirurgiao marcos = Fixtures.cirurgiaoDeQuarta();
		ConsultaEspecializada consulta = new ConsultaEspecializada(ana, marcos, Especialidade.ORTOPEDIA, Fixtures.QUARTA, LocalTime.of(15, 0), "Dor no joelho");

		assertThrows(RegraNegocioException.class, () -> marcos.adicionarConsulta(consulta));
	}

	@Test
	void consultaEspecializadaRespeitaDisponibilidadeEConflito() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		helena.adicionarConsulta(Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(10, 0)));
		ConsultaEspecializada mesmoHorario = Fixtures.cardiologia(carlos, helena, Fixtures.QUARTA, LocalTime.of(10, 0));
		ConsultaEspecializada foraDaAgenda = Fixtures.cardiologia(carlos, helena, Fixtures.QUINTA, LocalTime.of(10, 0));

		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(mesmoHorario));
		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(foraDaAgenda));
		assertEquals(1, helena.getConsultas().size());
	}

	@Test
	void consultaEspecializadaPrecisaDeEspecialidade() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();

		assertThrows(RegraNegocioException.class, () -> new ConsultaEspecializada(ana, helena, null, Fixtures.QUARTA, LocalTime.of(10, 0), "Palpitações"));
	}

	@Test
	void iniciarValidaDeNovoAsCondicoes() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		ConsultaEspecializada consulta = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(10, 0));
		helena.adicionarConsulta(consulta);
		consulta.confirmar();

		helena.setEspecialidade(Especialidade.NEUROLOGIA);

		assertThrows(RegraNegocioException.class, consulta::iniciar);
		assertEquals(SituacaoConsulta.CONFIRMADA, consulta.getSituacao());
	}

}
