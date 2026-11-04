package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class ProfissionalSaudeTest {

	@Test
	void atendeSomenteDentroDaDisponibilidade() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();

		assertTrue(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(8, 0)));
		assertTrue(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(13, 30)));
		assertFalse(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(14, 0)));
		assertFalse(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(7, 30)));
		assertFalse(helena.atendeNoHorario(Fixtures.QUINTA, LocalTime.of(10, 0)));
	}

	@Test
	void naoPermiteDuasConsultasNoMesmoHorario() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Paciente carlos = Fixtures.paciente("Carlos Eduardo Lima", "234.567.890-11");

		helena.adicionarConsulta(Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(10, 30)));
		ConsultaEspecializada mesmoHorario = Fixtures.cardiologia(carlos, helena, Fixtures.QUARTA, LocalTime.of(10, 30));

		assertFalse(helena.estaDisponivel(Fixtures.QUARTA, LocalTime.of(10, 30)));
		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(mesmoHorario));
		assertEquals(1, helena.getConsultas().size());
	}

	@Test
	void horarioDeConsultaCanceladaFicaLivreDeNovo() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		ConsultaEspecializada consulta = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0));
		helena.adicionarConsulta(consulta);

		consulta.cancelar();

		assertTrue(helena.estaDisponivel(Fixtures.QUARTA, LocalTime.of(9, 0)));
	}

	@Test
	void horarioDeNaoComparecimentoFicaLivreDeNovo() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		ConsultaEspecializada consulta = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0));
		helena.adicionarConsulta(consulta);

		consulta.registrarNaoComparecimento();

		assertTrue(helena.estaDisponivel(Fixtures.QUARTA, LocalTime.of(9, 0)));
	}

	@Test
	void naoAgendaForaDaDisponibilidade() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		ConsultaEspecializada naQuinta = Fixtures.cardiologia(ana, helena, Fixtures.QUINTA, LocalTime.of(9, 0));

		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(naQuinta));
	}

	@Test
	void naoAceitaConsultaDeOutroProfissionalNemRepetida() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Especialista outra = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		ConsultaEspecializada consulta = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0));

		assertThrows(RegraNegocioException.class, () -> outra.adicionarConsulta(consulta));
		helena.adicionarConsulta(consulta);
		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(consulta));
		assertEquals(1, helena.getConsultas().size());
	}

}
