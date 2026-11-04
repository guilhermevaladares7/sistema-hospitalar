package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.dto.InternacaoRequest;
import br.pucminas.sih.dto.QuartoResponse;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.SituacaoQuarto;
import br.pucminas.sih.model.TipoLeito;
import br.pucminas.sih.model.TipoQuarto;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuartoServiceTest {

	@Autowired
	private QuartoService quartoService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private InternacaoService internacaoService;

	@Autowired
	private EntityManager entityManager;

	@Test
	void cadastraCadaTipoComADiariaCalculada() {
		QuartoResponse enfermaria = quartoService.cadastrarEnfermaria(Dados.enfermaria("101", 4));
		QuartoResponse apartamento = quartoService.cadastrarApartamento(Dados.apartamento("301", TipoLeito.ELETRICO, true));
		QuartoResponse uti = quartoService.cadastrarUti(Dados.uti("401", 2, true));
		entityManager.flush();
		entityManager.clear();

		QuartoResponse enfermariaSalva = quartoService.buscar(enfermaria.id());
		QuartoResponse apartamentoSalvo = quartoService.buscar(apartamento.id());
		QuartoResponse utiSalva = quartoService.buscar(uti.id());

		assertEquals(TipoQuarto.ENFERMARIA, enfermariaSalva.tipo());
		assertEquals(4, enfermariaSalva.capacidadeMaxima());
		assertEquals(new BigDecimal("255.00"), enfermariaSalva.valorDiaria());
		assertNull(enfermariaSalva.valorDiariaComAcompanhante());
		assertFalse(enfermariaSalva.permiteAcompanhante());

		assertEquals(TipoQuarto.APARTAMENTO, apartamentoSalvo.tipo());
		assertEquals(TipoLeito.ELETRICO, apartamentoSalvo.tipoLeito());
		assertEquals(new BigDecimal("590.00"), apartamentoSalvo.valorDiaria());
		assertEquals(new BigDecimal("670.00"), apartamentoSalvo.valorDiariaComAcompanhante());

		assertEquals(TipoQuarto.UTI, utiSalva.tipo());
		assertTrue(utiSalva.possuiIsolamento());
		assertEquals(new BigDecimal("1700.00"), utiSalva.valorDiaria());
		assertEquals(3, quartoService.listar().size());
	}

	@Test
	void naoCadastraNumeroRepetido() {
		quartoService.cadastrarEnfermaria(Dados.enfermaria("101", 4));

		assertThrows(RegraNegocioException.class, () -> quartoService.cadastrarUti(Dados.uti("101", 2, false)));
	}

	@Test
	void regrasDoModeloContinuamValendo() {
		assertThrows(RegraNegocioException.class, () -> quartoService.cadastrarEnfermaria(Dados.enfermaria("101", 0)));
	}

	@Test
	void listaSoOsDisponiveis() {
		QuartoResponse apartamento = quartoService.cadastrarApartamento(Dados.apartamento("301", TipoLeito.PADRAO, false));
		QuartoResponse enfermaria = quartoService.cadastrarEnfermaria(Dados.enfermaria("101", 4));
		Long pacienteId = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00")).id();
		Long medicoId = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207")).id();
		LocalDate hoje = LocalDate.now();
		internacaoService.registrar(new InternacaoRequest(pacienteId, medicoId, apartamento.id(), hoje, hoje.plusDays(3), false));
		entityManager.flush();
		entityManager.clear();

		List<QuartoResponse> disponiveis = quartoService.listarDisponiveis();

		assertEquals(1, disponiveis.size());
		assertEquals(enfermaria.id(), disponiveis.get(0).id());
		assertEquals(SituacaoQuarto.OCUPADO, quartoService.buscar(apartamento.id()).situacao());
		assertEquals(1, quartoService.buscar(apartamento.id()).ocupacaoAtual());
	}

	@Test
	void quartoInexistenteLancaNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> quartoService.buscar(9999L));
	}

}
