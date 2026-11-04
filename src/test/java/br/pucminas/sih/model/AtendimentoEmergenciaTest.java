package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AtendimentoEmergenciaTest {

	private static final LocalDateTime DOMINGO_DE_MADRUGADA = LocalDateTime.of(2026, 10, 18, 3, 0);

	private final Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
	private final ClinicoGeral rafael = Fixtures.clinicoDeQuarta();

	private AtendimentoEmergencia chegada(Prioridade prioridade, int minutos) {
		return new AtendimentoEmergencia(ana, rafael, prioridade, DOMINGO_DE_MADRUGADA.plusMinutes(minutos));
	}

	@Test
	void comecaAguardandoAvaliacao() {
		AtendimentoEmergencia atendimento = chegada(Prioridade.ALTA, 0);

		assertEquals(SituacaoEmergencia.AGUARDANDO_AVALIACAO, atendimento.getSituacao());
		assertTrue(atendimento.aguardaAvaliacao());
	}

	@Test
	void naoDependeDaAgendaDoProfissional() {
		AtendimentoEmergencia atendimento = chegada(Prioridade.CRITICA, 0);

		assertFalse(rafael.atendeNoHorario(DOMINGO_DE_MADRUGADA.toLocalDate(), DOMINGO_DE_MADRUGADA.toLocalTime()));
		atendimento.iniciar();
		assertEquals(SituacaoEmergencia.EM_ATENDIMENTO, atendimento.getSituacao());
	}

	@Test
	void fluxoCompletoAteConcluir() {
		AtendimentoEmergencia atendimento = chegada(Prioridade.MODERADA, 0);

		atendimento.iniciar();
		atendimento.concluir("Sutura no antebraço");

		assertEquals(SituacaoEmergencia.CONCLUIDO, atendimento.getSituacao());
		assertEquals("Sutura no antebraço", atendimento.getObservacoes());
		assertFalse(atendimento.aguardaAvaliacao());
	}

	@Test
	void naoConcluiSemIniciar() {
		AtendimentoEmergencia atendimento = chegada(Prioridade.BAIXA, 0);

		assertThrows(RegraNegocioException.class, () -> atendimento.concluir("Alta"));
		assertEquals(SituacaoEmergencia.AGUARDANDO_AVALIACAO, atendimento.getSituacao());
	}

	@Test
	void soIniciaSeEstiverAguardando() {
		AtendimentoEmergencia atendimento = chegada(Prioridade.BAIXA, 0);
		atendimento.iniciar();

		assertThrows(RegraNegocioException.class, atendimento::iniciar);

		atendimento.concluir("Alta");
		assertThrows(RegraNegocioException.class, atendimento::iniciar);
		assertThrows(RegraNegocioException.class, () -> atendimento.concluir("De novo"));
	}

	@Test
	void exigePrioridadeEHoraDeChegada() {
		assertThrows(RegraNegocioException.class, () -> new AtendimentoEmergencia(ana, rafael, null, DOMINGO_DE_MADRUGADA));
		assertThrows(RegraNegocioException.class, () -> new AtendimentoEmergencia(ana, rafael, Prioridade.ALTA, null));
	}

	@Test
	void ordenaPorPrioridadeECriticaPrimeiroDepoisPorChegada() {
		AtendimentoEmergencia baixa = chegada(Prioridade.BAIXA, 0);
		AtendimentoEmergencia altaTarde = chegada(Prioridade.ALTA, 30);
		AtendimentoEmergencia critica = chegada(Prioridade.CRITICA, 40);
		AtendimentoEmergencia altaCedo = chegada(Prioridade.ALTA, 10);
		AtendimentoEmergencia moderada = chegada(Prioridade.MODERADA, 5);

		List<AtendimentoEmergencia> fila = new ArrayList<>(List.of(baixa, altaTarde, critica, altaCedo, moderada));
		fila.sort(AtendimentoEmergencia.ORDEM_DE_ATENDIMENTO);

		assertEquals(List.of(critica, altaCedo, altaTarde, moderada, baixa), fila);
	}

	@Test
	void prioridadeSabeQualEMaisUrgente() {
		assertTrue(Prioridade.CRITICA.ehMaisUrgenteQue(Prioridade.ALTA));
		assertTrue(Prioridade.MODERADA.ehMaisUrgenteQue(Prioridade.BAIXA));
		assertFalse(Prioridade.BAIXA.ehMaisUrgenteQue(Prioridade.BAIXA));
		assertFalse(Prioridade.ALTA.ehMaisUrgenteQue(Prioridade.CRITICA));
	}

	@Test
	void entraNoHistoricoDoPaciente() {
		AtendimentoEmergencia atendimento = chegada(Prioridade.ALTA, 0);
		ana.adicionarAtendimentoEmergencia(atendimento);

		assertTrue(ana.getHistorico().getAtendimentos().contains(atendimento));
	}

}
