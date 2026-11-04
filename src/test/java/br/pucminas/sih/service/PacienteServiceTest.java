package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.dto.HistoricoResponse;
import br.pucminas.sih.dto.PacienteResponse;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PacienteServiceTest {

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private EntityManager entityManager;

	@Test
	void cadastraEBuscaPaciente() {
		PacienteResponse cadastrado = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00"));
		entityManager.flush();
		entityManager.clear();

		PacienteResponse encontrado = pacienteService.buscar(cadastrado.id());

		assertEquals("Ana Beatriz Moreira", encontrado.nome());
		assertEquals("123.456.789-00", encontrado.cpf());
		assertEquals("Belo Horizonte", encontrado.endereco().cidade());
		assertFalse(encontrado.possuiInternacaoAtiva());
		assertEquals(cadastrado.id(), pacienteService.buscarPorCpf("123.456.789-00").id());
		assertEquals(1, pacienteService.listar().size());
	}

	@Test
	void naoCadastraCpfRepetido() {
		pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00"));

		assertThrows(RegraNegocioException.class, () -> pacienteService.cadastrar(Dados.paciente("Outra Ana", "123.456.789-00")));
	}

	@Test
	void pacienteInexistenteLancaNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> pacienteService.buscar(9999L));
		assertThrows(RecursoNaoEncontradoException.class, () -> pacienteService.buscarPorCpf("000.000.000-00"));
		assertThrows(RecursoNaoEncontradoException.class, () -> pacienteService.buscarHistorico(9999L));
	}

	@Test
	void historicoDePacienteNovoVemVazio() {
		PacienteResponse paciente = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00"));

		HistoricoResponse historico = pacienteService.buscarHistorico(paciente.id());

		assertEquals(paciente.id(), historico.paciente().id());
		assertTrue(historico.atendimentos().isEmpty());
		assertTrue(historico.encaminhamentos().isEmpty());
	}

}
