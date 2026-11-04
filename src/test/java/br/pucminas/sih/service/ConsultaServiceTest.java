package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucminas.sih.dto.ConsultaEspecializadaRequest;
import br.pucminas.sih.dto.ConsultaResponse;
import br.pucminas.sih.dto.ConsultaRotinaRequest;
import br.pucminas.sih.dto.EncaminhamentoRequest;
import br.pucminas.sih.dto.EncaminhamentoResponse;
import br.pucminas.sih.dto.HistoricoResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.ModalidadeConsulta;
import br.pucminas.sih.model.SituacaoConsulta;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ConsultaServiceTest {

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private EntityManager entityManager;

	private Long pacienteId;
	private Long clinicoId;
	private Long cardiologistaId;

	@BeforeEach
	void cadastrar() {
		pacienteId = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00")).id();
		clinicoId = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207")).id();
		cardiologistaId = profissionalService.cadastrarEspecialista(Dados.cardiologista("CRM-MG 45.812")).id();
	}

	private ConsultaRotinaRequest rotinaAs(int hora) {
		return new ConsultaRotinaRequest(pacienteId, clinicoId, Dados.PROXIMA_QUARTA, LocalTime.of(hora, 0), "Check-up");
	}

	private void limparContexto() {
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void rotinaPercorreTodasAsSituacoesAteConcluir() {
		ConsultaResponse agendada = consultaService.agendarRotina(rotinaAs(9));
		limparContexto();

		assertEquals(ModalidadeConsulta.ROTINA, agendada.modalidade());
		assertEquals(SituacaoConsulta.AGENDADA, agendada.situacao());
		assertEquals(SituacaoConsulta.CONFIRMADA, consultaService.confirmar(agendada.id()).situacao());
		assertEquals(SituacaoConsulta.EM_ATENDIMENTO, consultaService.iniciar(agendada.id()).situacao());
		ConsultaResponse concluida = consultaService.concluir(agendada.id(), new ObservacoesRequest("Pressão normal"));
		limparContexto();

		assertEquals(SituacaoConsulta.CONCLUIDA, concluida.situacao());
		assertEquals("Pressão normal", consultaService.buscar(agendada.id()).observacoes());
	}

	@Test
	void transicaoIncompativelLancaRegraNegocio() {
		ConsultaResponse consulta = consultaService.agendarRotina(rotinaAs(9));
		consultaService.cancelar(consulta.id());

		assertThrows(RegraNegocioException.class, () -> consultaService.confirmar(consulta.id()));
		assertThrows(RegraNegocioException.class, () -> consultaService.iniciar(consulta.id()));
		assertThrows(RegraNegocioException.class, () -> consultaService.registrarNaoComparecimento(consulta.id()));
	}

	@Test
	void naoAgendaDuasConsultasNoMesmoHorario() {
		consultaService.agendarRotina(rotinaAs(9));
		limparContexto();

		assertThrows(RegraNegocioException.class, () -> consultaService.agendarRotina(rotinaAs(9)));
	}

	@Test
	void naoComparecimentoLiberaOHorario() {
		ConsultaResponse primeira = consultaService.agendarRotina(rotinaAs(9));
		consultaService.registrarNaoComparecimento(primeira.id());
		limparContexto();

		ConsultaResponse segunda = consultaService.agendarRotina(rotinaAs(9));

		assertEquals(SituacaoConsulta.AGENDADA, segunda.situacao());
	}

	@Test
	void rotinaSoComClinicoGeral() {
		ConsultaRotinaRequest comEspecialista = new ConsultaRotinaRequest(pacienteId, cardiologistaId, Dados.PROXIMA_QUARTA, LocalTime.of(9, 0), "Check-up");

		assertThrows(RegraNegocioException.class, () -> consultaService.agendarRotina(comEspecialista));
	}

	@Test
	void especializadaExigeEspecialidadeCompativel() {
		ConsultaResponse cardiologia = consultaService.agendarEspecializada(new ConsultaEspecializadaRequest(pacienteId, cardiologistaId, Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(10, 0), "Palpitações"));
		ConsultaEspecializadaRequest neurologia = new ConsultaEspecializadaRequest(pacienteId, cardiologistaId, Especialidade.NEUROLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(11, 0), "Enxaqueca");

		assertEquals(ModalidadeConsulta.ESPECIALIZADA, cardiologia.modalidade());
		assertEquals(Especialidade.CARDIOLOGIA, cardiologia.especialidade());
		assertThrows(RegraNegocioException.class, () -> consultaService.agendarEspecializada(neurologia));
	}

	@Test
	void foraDaAgendaDoProfissionalLancaRegraNegocio() {
		assertThrows(RegraNegocioException.class, () -> consultaService.agendarRotina(rotinaAs(15)));
		assertThrows(RegraNegocioException.class, () -> consultaService.agendarRotina(new ConsultaRotinaRequest(pacienteId, clinicoId, Dados.PROXIMA_QUARTA.plusDays(1), LocalTime.of(9, 0), "Check-up")));
	}

	@Test
	void encaminhaAPartirDaRotinaConcluidaEApareceNoHistorico() {
		ConsultaResponse consulta = consultaService.agendarRotina(rotinaAs(9));
		consultaService.confirmar(consulta.id());
		consultaService.iniciar(consulta.id());
		consultaService.concluir(consulta.id(), new ObservacoesRequest("Sopro cardíaco"));
		limparContexto();

		EncaminhamentoResponse encaminhamento = consultaService.encaminhar(consulta.id(), new EncaminhamentoRequest(Especialidade.CARDIOLOGIA, "Investigar sopro"));
		limparContexto();
		HistoricoResponse historico = pacienteService.buscarHistorico(pacienteId);

		assertEquals(clinicoId, encaminhamento.clinicoId());
		assertEquals(pacienteId, encaminhamento.pacienteId());
		assertEquals(Especialidade.CARDIOLOGIA, encaminhamento.especialidadeDestino());
		assertEquals(1, historico.atendimentos().size());
		assertEquals("Sopro cardíaco", historico.atendimentos().get(0).observacoes());
		assertEquals(1, historico.encaminhamentos().size());
	}

	@Test
	void naoEncaminhaConsultaNaoConcluidaNemEspecializada() {
		ConsultaResponse rotina = consultaService.agendarRotina(rotinaAs(9));
		ConsultaResponse especializada = consultaService.agendarEspecializada(new ConsultaEspecializadaRequest(pacienteId, cardiologistaId, Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(10, 0), "Palpitações"));
		EncaminhamentoRequest request = new EncaminhamentoRequest(Especialidade.CARDIOLOGIA, "Investigar sopro");

		assertThrows(RegraNegocioException.class, () -> consultaService.encaminhar(rotina.id(), request));
		assertThrows(RecursoNaoEncontradoException.class, () -> consultaService.encaminhar(especializada.id(), request));
	}

	@Test
	void referenciasInexistentesLancamNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> consultaService.buscar(9999L));
		assertThrows(RecursoNaoEncontradoException.class, () -> consultaService.cancelar(9999L));
		assertThrows(RecursoNaoEncontradoException.class, () -> consultaService.agendarRotina(new ConsultaRotinaRequest(9999L, clinicoId, Dados.PROXIMA_QUARTA, LocalTime.of(9, 0), "Check-up")));
		assertThrows(RecursoNaoEncontradoException.class, () -> consultaService.agendarRotina(new ConsultaRotinaRequest(pacienteId, 9999L, Dados.PROXIMA_QUARTA, LocalTime.of(9, 0), "Check-up")));
	}

	@Test
	void listaPorData() {
		consultaService.agendarRotina(rotinaAs(9));
		consultaService.agendarRotina(rotinaAs(10));

		assertEquals(2, consultaService.listarPorData(Dados.PROXIMA_QUARTA).size());
		assertEquals(0, consultaService.listarPorData(Dados.PROXIMA_QUARTA.plusDays(1)).size());
		assertEquals(2, consultaService.listar().size());
	}

}
