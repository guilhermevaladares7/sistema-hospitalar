package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class HistoricoMedicoTest {

	@Test
	void reuneConsultasEInternacoesDoMaisRecenteParaOMaisAntigo() {
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Quarto quarto = new Quarto("104", 1, 4);

		Consulta antiga = new Consulta(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0), "Check-up");
		helena.adicionarConsulta(antiga);
		ana.adicionarConsulta(antiga);
		antiga.realizar("ECG sem alterações");

		Consulta cancelada = new Consulta(ana, helena, Fixtures.QUARTA.plusWeeks(1), LocalTime.of(9, 0), "Retorno");
		helena.adicionarConsulta(cancelada);
		ana.adicionarConsulta(cancelada);
		cancelada.cancelar();

		Internacao internacao = new Internacao(ana, helena, quarto, Fixtures.QUARTA.plusWeeks(2), Fixtures.QUARTA.plusWeeks(3));
		quarto.ocupar(internacao);
		ana.adicionarInternacao(internacao);

		HistoricoMedico historico = ana.getHistorico();
		List<Atendimento> atendimentos = historico.getAtendimentos();

		assertEquals(3, atendimentos.size());
		assertSame(internacao, atendimentos.get(0));
		assertSame(antiga, atendimentos.get(2));
		assertEquals(1, historico.getConsultasRealizadas().size());
		assertEquals(1, historico.getInternacoes().size());
		assertTrue(ana.possuiInternacaoAtiva());
	}

	@Test
	void pacienteNaoTemDuasInternacoesAtivas() {
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Quarto quarto = new Quarto("104", 1, 4);

		Internacao primeira = new Internacao(ana, helena, quarto, Fixtures.QUARTA, Fixtures.QUINTA);
		ana.adicionarInternacao(primeira);
		Internacao segunda = new Internacao(ana, helena, quarto, Fixtures.QUARTA, Fixtures.QUINTA);

		assertThrows(RegraNegocioException.class, () -> ana.adicionarInternacao(segunda));
	}

	@Test
	void consultaRealizadaNaoPodeSerCancelada() {
		ProfissionalSaude helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Consulta consulta = new Consulta(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0), "Check-up");

		consulta.realizar("Tudo certo");

		assertEquals(StatusConsulta.REALIZADA, consulta.getStatus());
		assertThrows(RegraNegocioException.class, consulta::cancelar);
		assertFalse(consulta.getObservacoes().isBlank());
	}

}
