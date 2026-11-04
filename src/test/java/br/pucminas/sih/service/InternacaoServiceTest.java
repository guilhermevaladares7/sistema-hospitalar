package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.dto.AltaRequest;
import br.pucminas.sih.dto.InternacaoRequest;
import br.pucminas.sih.dto.InternacaoResponse;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.SituacaoQuarto;
import br.pucminas.sih.model.TipoLeito;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class InternacaoServiceTest {

	private static final LocalDate ENTRADA = LocalDate.of(2026, 10, 14);

	@Autowired
	private InternacaoService internacaoService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private QuartoService quartoService;

	@Autowired
	private EntityManager entityManager;

	private Long anaId;
	private Long carlosId;
	private Long medicoId;
	private Long apartamentoId;
	private Long enfermariaId;

	@BeforeEach
	void cadastrar() {
		anaId = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00")).id();
		carlosId = pacienteService.cadastrar(Dados.paciente("Carlos Henrique Souza", "234.567.890-11")).id();
		medicoId = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207")).id();
		apartamentoId = quartoService.cadastrarApartamento(Dados.apartamento("303", TipoLeito.ELETRICO, true)).id();
		enfermariaId = quartoService.cadastrarEnfermaria(Dados.enfermaria("104", 4)).id();
	}

	private void limparContexto() {
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void registraComAcompanhanteEDaAltaCalculandoOTotal() {
		InternacaoResponse registrada = internacaoService.registrar(new InternacaoRequest(anaId, medicoId, apartamentoId, ENTRADA, ENTRADA.plusDays(5), true));
		limparContexto();

		assertTrue(registrada.ativa());
		assertEquals(5, registrada.diasCobrados());
		assertEquals(new BigDecimal("670.00"), registrada.valorDiaria());
		assertEquals(new BigDecimal("3350.00"), registrada.valorTotal());
		assertTrue(pacienteService.buscar(anaId).possuiInternacaoAtiva());
		assertEquals(SituacaoQuarto.OCUPADO, quartoService.buscar(apartamentoId).situacao());

		InternacaoResponse finalizada = internacaoService.darAlta(registrada.id(), new AltaRequest(ENTRADA.plusDays(3)));
		limparContexto();

		assertFalse(finalizada.ativa());
		assertEquals(3, finalizada.diasCobrados());
		assertEquals(new BigDecimal("2010.00"), internacaoService.buscar(registrada.id()).valorTotal());
		assertEquals(SituacaoQuarto.DISPONIVEL, quartoService.buscar(apartamentoId).situacao());
		assertFalse(pacienteService.buscar(anaId).possuiInternacaoAtiva());
		assertEquals(0, internacaoService.listarAtivas().size());
		assertEquals(1, internacaoService.listar().size());
	}

	@Test
	void pacienteNaoTemDuasInternacoesAtivas() {
		internacaoService.registrar(new InternacaoRequest(anaId, medicoId, enfermariaId, ENTRADA, ENTRADA.plusDays(2), false));
		limparContexto();

		assertThrows(RegraNegocioException.class, () -> internacaoService.registrar(new InternacaoRequest(anaId, medicoId, apartamentoId, ENTRADA, ENTRADA.plusDays(2), false)));
	}

	@Test
	void quartoLotadoNaoRecebeOutroPaciente() {
		internacaoService.registrar(new InternacaoRequest(anaId, medicoId, apartamentoId, ENTRADA, ENTRADA.plusDays(2), false));
		limparContexto();

		assertThrows(RegraNegocioException.class, () -> internacaoService.registrar(new InternacaoRequest(carlosId, medicoId, apartamentoId, ENTRADA, ENTRADA.plusDays(2), false)));
	}

	@Test
	void enfermariaNaoAceitaAcompanhante() {
		assertThrows(RegraNegocioException.class, () -> internacaoService.registrar(new InternacaoRequest(anaId, medicoId, enfermariaId, ENTRADA, ENTRADA.plusDays(2), true)));
	}

	@Test
	void naoDaAltaDuasVezes() {
		InternacaoResponse registrada = internacaoService.registrar(new InternacaoRequest(anaId, medicoId, enfermariaId, ENTRADA, ENTRADA.plusDays(2), false));
		internacaoService.darAlta(registrada.id(), new AltaRequest(ENTRADA.plusDays(1)));

		assertThrows(RegraNegocioException.class, () -> internacaoService.darAlta(registrada.id(), new AltaRequest(ENTRADA.plusDays(2))));
	}

	@Test
	void referenciasInexistentesLancamNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> internacaoService.registrar(new InternacaoRequest(anaId, medicoId, 9999L, ENTRADA, ENTRADA.plusDays(2), false)));
		assertThrows(RecursoNaoEncontradoException.class, () -> internacaoService.darAlta(9999L, new AltaRequest(ENTRADA)));
	}

}
