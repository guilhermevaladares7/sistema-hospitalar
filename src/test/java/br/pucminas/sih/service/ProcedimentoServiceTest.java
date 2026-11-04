package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucminas.sih.dto.ConclusaoProcedimentoRequest;
import br.pucminas.sih.dto.InicioProcedimentoRequest;
import br.pucminas.sih.dto.ProcedimentoRequest;
import br.pucminas.sih.dto.ProcedimentoResponse;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.SituacaoProcedimento;
import br.pucminas.sih.model.TipoCirurgia;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProcedimentoServiceTest {

	private static final LocalDateTime PREVISTO = Dados.PROXIMA_QUARTA.atTime(14, 0);

	@Autowired
	private ProcedimentoService procedimentoService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private EntityManager entityManager;

	private Long pacienteId;
	private Long cirurgiaoId;
	private Long clinicoId;

	@BeforeEach
	void cadastrar() {
		pacienteId = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00")).id();
		cirurgiaoId = profissionalService.cadastrarCirurgiao(Dados.cirurgiao("CRM-MG 51.334")).id();
		clinicoId = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207")).id();
	}

	private ProcedimentoRequest artroscopia(Long profissionalId, LocalDateTime previsto) {
		return new ProcedimentoRequest(pacienteId, profissionalId, "Artroscopia de joelho", TipoCirurgia.ELETIVA, previsto, "Jejum de 8 horas");
	}

	@Test
	void agendaIniciaEConcluiProcedimento() {
		ProcedimentoResponse agendado = procedimentoService.agendar(artroscopia(cirurgiaoId, PREVISTO));
		entityManager.flush();
		entityManager.clear();

		assertEquals(SituacaoProcedimento.AGENDADO, agendado.situacao());
		assertEquals(SituacaoProcedimento.EM_ANDAMENTO, procedimentoService.iniciar(agendado.id(), new InicioProcedimentoRequest(PREVISTO.plusMinutes(10))).situacao());
		ProcedimentoResponse concluido = procedimentoService.concluir(agendado.id(), new ConclusaoProcedimentoRequest(PREVISTO.plusHours(2), "Sem intercorrências"));
		entityManager.flush();
		entityManager.clear();

		assertEquals(SituacaoProcedimento.CONCLUIDO, concluido.situacao());
		assertEquals(PREVISTO.plusMinutes(10), concluido.dataHoraInicio());
		assertEquals("Sem intercorrências", procedimentoService.buscar(agendado.id()).observacoesPosOperatorias());
		assertEquals(1, procedimentoService.listarRealizadosPorCirurgiao(cirurgiaoId).size());
	}

	@Test
	void soCirurgiaoAgendaProcedimento() {
		assertThrows(RegraNegocioException.class, () -> procedimentoService.agendar(artroscopia(clinicoId, PREVISTO)));
		assertThrows(RecursoNaoEncontradoException.class, () -> procedimentoService.listarRealizadosPorCirurgiao(clinicoId));
	}

	@Test
	void naoAgendaDoisProcedimentosNoMesmoHorario() {
		procedimentoService.agendar(artroscopia(cirurgiaoId, PREVISTO));
		entityManager.flush();
		entityManager.clear();

		assertThrows(RegraNegocioException.class, () -> procedimentoService.agendar(artroscopia(cirurgiaoId, PREVISTO)));
	}

	@Test
	void naoConcluiSemIniciarECanceladoLiberaOHorario() {
		ProcedimentoResponse agendado = procedimentoService.agendar(artroscopia(cirurgiaoId, PREVISTO));

		assertThrows(RegraNegocioException.class, () -> procedimentoService.concluir(agendado.id(), new ConclusaoProcedimentoRequest(PREVISTO.plusHours(2), "Sem intercorrências")));

		assertEquals(SituacaoProcedimento.CANCELADO, procedimentoService.cancelar(agendado.id()).situacao());
		entityManager.flush();
		entityManager.clear();

		assertEquals(SituacaoProcedimento.AGENDADO, procedimentoService.agendar(artroscopia(cirurgiaoId, PREVISTO)).situacao());
		assertEquals(1, procedimentoService.listarPorSituacao(SituacaoProcedimento.CANCELADO).size());
		assertEquals(0, procedimentoService.listarRealizadosPorCirurgiao(cirurgiaoId).size());
	}

	@Test
	void procedimentoInexistenteLancaNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> procedimentoService.cancelar(9999L));
	}

}
