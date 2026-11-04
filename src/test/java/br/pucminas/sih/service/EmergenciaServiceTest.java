package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucminas.sih.dto.EmergenciaRequest;
import br.pucminas.sih.dto.EmergenciaResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.Prioridade;
import br.pucminas.sih.model.SituacaoEmergencia;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmergenciaServiceTest {

	@Autowired
	private EmergenciaService emergenciaService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private EntityManager entityManager;

	private Long anaId;
	private Long carlosId;
	private Long fernandaId;
	private Long clinicoId;

	@BeforeEach
	void cadastrar() {
		anaId = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00")).id();
		carlosId = pacienteService.cadastrar(Dados.paciente("Carlos Henrique Souza", "234.567.890-11")).id();
		fernandaId = pacienteService.cadastrar(Dados.paciente("Fernanda Lima", "345.678.901-22")).id();
		clinicoId = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207")).id();
	}

	@Test
	void listaAguardandoDaMaiorParaAMenorPrioridade() {
		EmergenciaResponse baixa = emergenciaService.abrir(new EmergenciaRequest(anaId, clinicoId, Prioridade.BAIXA));
		EmergenciaResponse critica = emergenciaService.abrir(new EmergenciaRequest(carlosId, clinicoId, Prioridade.CRITICA));
		EmergenciaResponse alta = emergenciaService.abrir(new EmergenciaRequest(fernandaId, clinicoId, Prioridade.ALTA));
		entityManager.flush();
		entityManager.clear();

		List<EmergenciaResponse> fila = emergenciaService.listarAguardandoPorPrioridade();

		assertEquals(List.of(critica.id(), alta.id(), baixa.id()), List.of(fila.get(0).id(), fila.get(1).id(), fila.get(2).id()));
		assertEquals(SituacaoEmergencia.AGUARDANDO_AVALIACAO, baixa.situacao());
	}

	@Test
	void iniciarEConcluirMudamASituacao() {
		EmergenciaResponse aberta = emergenciaService.abrir(new EmergenciaRequest(anaId, clinicoId, Prioridade.MODERADA));

		assertEquals(SituacaoEmergencia.EM_ATENDIMENTO, emergenciaService.iniciar(aberta.id()).situacao());
		assertEquals(0, emergenciaService.listarAguardandoPorPrioridade().size());
		assertEquals(1, emergenciaService.listarPorSituacao(SituacaoEmergencia.EM_ATENDIMENTO).size());

		EmergenciaResponse concluida = emergenciaService.concluir(aberta.id(), new ObservacoesRequest("Sutura no antebraço"));
		entityManager.flush();
		entityManager.clear();

		assertEquals(SituacaoEmergencia.CONCLUIDO, concluida.situacao());
		assertEquals("Sutura no antebraço", emergenciaService.buscar(aberta.id()).observacoes());
		assertEquals(1, pacienteService.buscarHistorico(anaId).atendimentos().size());
	}

	@Test
	void naoConcluiSemIniciar() {
		EmergenciaResponse aberta = emergenciaService.abrir(new EmergenciaRequest(anaId, clinicoId, Prioridade.ALTA));

		assertThrows(RegraNegocioException.class, () -> emergenciaService.concluir(aberta.id(), new ObservacoesRequest("Alta")));
	}

	@Test
	void atendimentoInexistenteLancaNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> emergenciaService.iniciar(9999L));
		assertThrows(RecursoNaoEncontradoException.class, () -> emergenciaService.abrir(new EmergenciaRequest(9999L, clinicoId, Prioridade.ALTA)));
	}

}
