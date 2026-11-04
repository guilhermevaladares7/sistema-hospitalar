package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Quarto quarto = new Quarto("104", 1, 4);

		ConsultaEspecializada antiga = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA, LocalTime.of(9, 0));
		helena.adicionarConsulta(antiga);
		ana.adicionarConsulta(antiga);
		antiga.confirmar();
		antiga.iniciar();
		antiga.concluir("ECG sem alterações");

		ConsultaEspecializada cancelada = Fixtures.cardiologia(ana, helena, Fixtures.QUARTA.plusWeeks(1), LocalTime.of(9, 0));
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
		assertEquals(List.of(antiga), historico.getConsultasConcluidas());
		assertEquals(1, historico.getInternacoes().size());
		assertTrue(ana.possuiInternacaoAtiva());
	}

	@Test
	void pacienteNaoTemDuasInternacoesAtivas() {
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		Quarto quarto = new Quarto("104", 1, 4);

		Internacao primeira = new Internacao(ana, helena, quarto, Fixtures.QUARTA, Fixtures.QUINTA);
		ana.adicionarInternacao(primeira);
		Internacao segunda = new Internacao(ana, helena, quarto, Fixtures.QUARTA, Fixtures.QUINTA);

		assertThrows(RegraNegocioException.class, segunda::validarRealizacao);
		assertThrows(RegraNegocioException.class, () -> ana.adicionarInternacao(segunda));
	}

}
