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
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();

		assertTrue(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(8, 0)));
		assertTrue(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(13, 30)));
		assertFalse(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(14, 0)));
		assertFalse(helena.atendeNoHorario(Fixtures.QUARTA, LocalTime.of(7, 30)));
		assertFalse(helena.atendeNoHorario(Fixtures.QUINTA, LocalTime.of(10, 0)));
	}

	@Test
	void naoPermiteDuasConsultasNoMesmoHorario() {
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Paciente carlos = Fixtures.paciente("Carlos Eduardo Lima", "234.567.890-11");

		helena.adicionarConsulta(new Consulta(ana, helena, Fixtures.QUARTA, LocalTime.of(10, 30), "Palpitações"));
		Consulta mesmoHorario = new Consulta(carlos, helena, Fixtures.QUARTA, LocalTime.of(10, 30), "Check-up");

		assertFalse(helena.estaDisponivel(Fixtures.QUARTA, LocalTime.of(10, 30)));
		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(mesmoHorario));
		assertEquals(1, helena.getConsultas().size());
	}

	@Test
	void horarioDeConsultaCanceladaFicaLivreDeNovo() {
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Consulta consulta = new Consulta(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0), "Retorno");
		helena.adicionarConsulta(consulta);

		consulta.cancelar();

		assertTrue(helena.estaDisponivel(Fixtures.QUARTA, LocalTime.of(9, 0)));
	}

	@Test
	void naoAgendaForaDaDisponibilidade() {
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Consulta naQuinta = new Consulta(ana, helena, Fixtures.QUINTA, LocalTime.of(9, 0), "Retorno");

		assertThrows(RegraNegocioException.class, () -> helena.adicionarConsulta(naQuinta));
	}

}
