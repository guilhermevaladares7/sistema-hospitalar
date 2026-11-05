package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.dto.AltaRequest;
import br.pucminas.sih.dto.ConsultaEspecializadaRequest;
import br.pucminas.sih.dto.ConsultaResponse;
import br.pucminas.sih.dto.EmergenciaRequest;
import br.pucminas.sih.dto.EmergenciaResponse;
import br.pucminas.sih.dto.HistoricoResponse;
import br.pucminas.sih.dto.InternacaoRequest;
import br.pucminas.sih.dto.InternacaoResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Prioridade;
import br.pucminas.sih.model.SituacaoConsulta;
import br.pucminas.sih.model.SituacaoEmergencia;
import br.pucminas.sih.model.SituacaoQuarto;
import br.pucminas.sih.model.TipoLeito;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FluxoAtendimentoIntegrationTest {

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private QuartoService quartoService;

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private InternacaoService internacaoService;

	@Autowired
	private EmergenciaService emergenciaService;

	@Autowired
	private EntityManager entityManager;

	private void limparContexto() {
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void pacientePassaPorConsultaInternacaoEEmergenciaETudoVaiParaOHistorico() {
		Long pacienteId = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00")).id();
		Long cardiologistaId = profissionalService.cadastrarEspecialista(Dados.cardiologista("CRM-MG 45.812")).id();
		Long clinicoId = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207")).id();
		Long apartamentoId = quartoService.cadastrarApartamento(Dados.apartamento("303", TipoLeito.ELETRICO, true)).id();
		limparContexto();

		ConsultaResponse agendada = consultaService.agendarEspecializada(new ConsultaEspecializadaRequest(pacienteId, cardiologistaId, Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(10, 0), "Palpitações"));
		consultaService.confirmar(agendada.id());
		consultaService.iniciar(agendada.id());
		ConsultaResponse consultaConcluida = consultaService.concluir(agendada.id(), new ObservacoesRequest("Solicitada internação para observação."));
		limparContexto();

		assertEquals(SituacaoConsulta.CONCLUIDA, consultaConcluida.situacao());

		InternacaoResponse internacao = internacaoService.registrar(new InternacaoRequest(pacienteId, cardiologistaId, apartamentoId, Dados.PROXIMA_QUARTA, Dados.PROXIMA_QUARTA.plusDays(4), true));
		limparContexto();

		assertTrue(internacao.ativa());
		assertEquals(SituacaoQuarto.OCUPADO, quartoService.buscar(apartamentoId).situacao());
		assertTrue(pacienteService.buscar(pacienteId).possuiInternacaoAtiva());

		InternacaoResponse alta = internacaoService.darAlta(internacao.id(), new AltaRequest(Dados.PROXIMA_QUARTA.plusDays(2)));
		limparContexto();

		assertFalse(alta.ativa());
		assertEquals(SituacaoQuarto.DISPONIVEL, quartoService.buscar(apartamentoId).situacao());
		assertFalse(pacienteService.buscar(pacienteId).possuiInternacaoAtiva());

		EmergenciaResponse emergencia = emergenciaService.abrir(new EmergenciaRequest(pacienteId, clinicoId, Prioridade.ALTA));
		emergenciaService.iniciar(emergencia.id());
		EmergenciaResponse emergenciaConcluida = emergenciaService.concluir(emergencia.id(), new ObservacoesRequest("Estabilizada e liberada."));
		limparContexto();

		assertEquals(SituacaoEmergencia.CONCLUIDO, emergenciaConcluida.situacao());

		HistoricoResponse historico = pacienteService.buscarHistorico(pacienteId);

		assertEquals(3, historico.atendimentos().size());
	}

}
