package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcedimentoCirurgicoTest {

	private static final LocalDateTime PREVISTO = LocalDateTime.of(2026, 10, 20, 7, 0);

	private Paciente ana;
	private Cirurgiao marcos;
	private ProcedimentoCirurgico procedimento;

	@BeforeEach
	void agendar() {
		ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		marcos = Fixtures.cirurgiaoDeQuarta();
		procedimento = artroscopia(ana, marcos, PREVISTO);
		marcos.adicionarProcedimento(procedimento);
	}

	private ProcedimentoCirurgico artroscopia(Paciente paciente, ProfissionalSaude profissional, LocalDateTime previsto) {
		return new ProcedimentoCirurgico(paciente, profissional, "Artroscopia de joelho", TipoCirurgia.ELETIVA, previsto, "Jejum de 8 horas");
	}

	@Test
	void comecaAgendadoSemDatasEfetivas() {
		assertEquals(SituacaoProcedimento.AGENDADO, procedimento.getSituacao());
		assertNull(procedimento.getDataHoraInicio());
		assertNull(procedimento.getDataHoraFim());
		assertEquals("Jejum de 8 horas", procedimento.getObservacoesPreOperatorias());
	}

	@Test
	void fluxoCompletoRegistraDatasEfetivasEObservacoes() {
		LocalDateTime inicio = PREVISTO.plusMinutes(20);
		LocalDateTime fim = inicio.plusHours(2);

		procedimento.iniciar(inicio);
		assertEquals(SituacaoProcedimento.EM_ANDAMENTO, procedimento.getSituacao());
		assertEquals(inicio, procedimento.getDataHoraInicio());

		procedimento.concluir(fim, "Sem intercorrências");
		assertEquals(SituacaoProcedimento.CONCLUIDO, procedimento.getSituacao());
		assertEquals(fim, procedimento.getDataHoraFim());
		assertEquals("Sem intercorrências", procedimento.getObservacoesPosOperatorias());
	}

	@Test
	void naoConcluiSemTerIniciado() {
		assertThrows(RegraNegocioException.class, () -> procedimento.concluir(PREVISTO.plusHours(2), "Sem intercorrências"));
		assertEquals(SituacaoProcedimento.AGENDADO, procedimento.getSituacao());
		assertNull(procedimento.getDataHoraFim());
	}

	@Test
	void terminoNaoPodeSerAntesDoInicio() {
		procedimento.iniciar(PREVISTO);

		assertThrows(RegraNegocioException.class, () -> procedimento.concluir(PREVISTO.minusMinutes(1), "Sem intercorrências"));
		assertEquals(SituacaoProcedimento.EM_ANDAMENTO, procedimento.getSituacao());
	}

	@Test
	void entraNosRealizadosDoCirurgiaoSomenteAoConcluir() {
		assertEquals(List.of(), marcos.getProcedimentosRealizados());

		procedimento.iniciar(PREVISTO);
		assertEquals(List.of(), marcos.getProcedimentosRealizados());

		procedimento.concluir(PREVISTO.plusHours(2), "Sem intercorrências");
		assertEquals(List.of(procedimento), marcos.getProcedimentosRealizados());
	}

	@Test
	void cancelaSomenteAgendado() {
		ProcedimentoCirurgico outro = artroscopia(ana, marcos, PREVISTO.plusDays(1));
		marcos.adicionarProcedimento(outro);
		outro.cancelar();
		assertEquals(SituacaoProcedimento.CANCELADO, outro.getSituacao());

		procedimento.iniciar(PREVISTO);
		assertThrows(RegraNegocioException.class, procedimento::cancelar);
		assertThrows(RegraNegocioException.class, () -> outro.iniciar(PREVISTO.plusDays(1)));
		assertThrows(RegraNegocioException.class, () -> outro.concluir(PREVISTO.plusDays(1), "x"));
	}

	@Test
	void concluidoNaoMudaMais() {
		procedimento.iniciar(PREVISTO);
		procedimento.concluir(PREVISTO.plusHours(2), "Sem intercorrências");

		assertThrows(RegraNegocioException.class, procedimento::cancelar);
		assertThrows(RegraNegocioException.class, () -> procedimento.iniciar(PREVISTO));
		assertThrows(RegraNegocioException.class, () -> procedimento.registrarObservacoesPreOperatorias("Tarde demais"));
	}

	@Test
	void somenteCirurgiaoRealizaProcedimento() {
		ClinicoGeral rafael = Fixtures.clinicoDeQuarta();
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		ProcedimentoCirurgico comClinico = artroscopia(ana, rafael, PREVISTO);
		ProcedimentoCirurgico comEspecialista = artroscopia(ana, helena, PREVISTO);

		assertThrows(RegraNegocioException.class, comClinico::validarRealizacao);
		assertThrows(RegraNegocioException.class, comEspecialista::validarRealizacao);
		assertThrows(RegraNegocioException.class, () -> comClinico.iniciar(PREVISTO));
	}

	@Test
	void cirurgiaoNaoTemDoisProcedimentosNoMesmoHorarioPrevisto() {
		Paciente carlos = Fixtures.paciente("Carlos Eduardo Lima", "234.567.890-11");
		ProcedimentoCirurgico mesmoHorario = artroscopia(carlos, marcos, PREVISTO);
		ProcedimentoCirurgico outroHorario = artroscopia(carlos, marcos, PREVISTO.plusHours(3));

		assertThrows(RegraNegocioException.class, () -> marcos.adicionarProcedimento(mesmoHorario));
		marcos.adicionarProcedimento(outroHorario);
		assertEquals(2, marcos.getProcedimentos().size());
	}

	@Test
	void horarioDeProcedimentoCanceladoFicaLivre() {
		procedimento.cancelar();
		ProcedimentoCirurgico novo = artroscopia(ana, marcos, PREVISTO);

		marcos.adicionarProcedimento(novo);

		assertEquals(2, marcos.getProcedimentos().size());
	}

	@Test
	void naoAceitaProcedimentoDeOutroCirurgiaoNemRepetido() {
		Cirurgiao outro = Fixtures.cirurgiaoDeQuarta();

		assertThrows(RegraNegocioException.class, () -> outro.adicionarProcedimento(procedimento));
		assertThrows(RegraNegocioException.class, () -> marcos.adicionarProcedimento(procedimento));
	}

	@Test
	void exigeDescricaoTipoEDataPrevista() {
		assertThrows(RegraNegocioException.class, () -> new ProcedimentoCirurgico(ana, marcos, " ", TipoCirurgia.ELETIVA, PREVISTO, null));
		assertThrows(RegraNegocioException.class, () -> new ProcedimentoCirurgico(ana, marcos, "Artroscopia", null, PREVISTO, null));
		assertThrows(RegraNegocioException.class, () -> new ProcedimentoCirurgico(ana, marcos, "Artroscopia", TipoCirurgia.ELETIVA, null, null));
	}

	@Test
	void observacoesPreOperatoriasSoAntesDeIniciar() {
		procedimento.registrarObservacoesPreOperatorias("Suspender anticoagulante");
		assertEquals("Suspender anticoagulante", procedimento.getObservacoesPreOperatorias());

		procedimento.iniciar(PREVISTO);
		assertThrows(RegraNegocioException.class, () -> procedimento.registrarObservacoesPreOperatorias("Tarde demais"));
	}

	@Test
	void entraNoHistoricoDoPaciente() {
		ana.adicionarProcedimento(procedimento);

		assertTrue(ana.getHistorico().getAtendimentos().contains(procedimento));
	}

}
