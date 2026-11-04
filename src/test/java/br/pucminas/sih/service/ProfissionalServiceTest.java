package br.pucminas.sih.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.dto.ConsultaEspecializadaRequest;
import br.pucminas.sih.dto.PacienteResponse;
import br.pucminas.sih.dto.ProfissionalResponse;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.AreaCirurgica;
import br.pucminas.sih.model.CategoriaProfissional;
import br.pucminas.sih.model.Especialidade;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProfissionalServiceTest {

	@Autowired
	private ProfissionalService profissionalService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private EntityManager entityManager;

	@Test
	void cadastraCadaCategoria() {
		ProfissionalResponse clinico = profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207"));
		ProfissionalResponse especialista = profissionalService.cadastrarEspecialista(Dados.cardiologista("CRM-MG 45.812"));
		ProfissionalResponse cirurgiao = profissionalService.cadastrarCirurgiao(Dados.cirurgiao("CRM-MG 51.334"));
		entityManager.flush();
		entityManager.clear();

		ProfissionalResponse clinicoSalvo = profissionalService.buscar(clinico.id());
		ProfissionalResponse especialistaSalvo = profissionalService.buscar(especialista.id());
		ProfissionalResponse cirurgiaoSalvo = profissionalService.buscar(cirurgiao.id());

		assertEquals(CategoriaProfissional.CLINICO_GERAL, clinicoSalvo.categoria());
		assertNull(clinicoSalvo.especialidade());
		assertEquals(1, clinicoSalvo.disponibilidades().size());
		assertEquals(CategoriaProfissional.ESPECIALISTA, especialistaSalvo.categoria());
		assertEquals(Especialidade.CARDIOLOGIA, especialistaSalvo.especialidade());
		assertEquals(CategoriaProfissional.CIRURGIAO, cirurgiaoSalvo.categoria());
		assertEquals(AreaCirurgica.ORTOPEDICA, cirurgiaoSalvo.areaCirurgica());
		assertEquals(3, profissionalService.listar().size());
	}

	@Test
	void naoCadastraRegistroRepetido() {
		profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207"));

		assertThrows(RegraNegocioException.class, () -> profissionalService.cadastrarEspecialista(Dados.cardiologista("CRM-MG 38.207")));
	}

	@Test
	void buscaPorEspecialidade() {
		ProfissionalResponse cardiologista = profissionalService.cadastrarEspecialista(Dados.cardiologista("CRM-MG 45.812"));
		profissionalService.cadastrarClinicoGeral(Dados.clinico("CRM-MG 38.207"));

		List<ProfissionalResponse> cardiologistas = profissionalService.listarPorEspecialidade(Especialidade.CARDIOLOGIA);

		assertEquals(1, cardiologistas.size());
		assertEquals(cardiologista.id(), cardiologistas.get(0).id());
		assertTrue(profissionalService.listarPorEspecialidade(Especialidade.NEUROLOGIA).isEmpty());
	}

	@Test
	void disponiveisPorEspecialidadeConsideraAgendaEConsultas() {
		ProfissionalResponse cardiologista = profissionalService.cadastrarEspecialista(Dados.cardiologista("CRM-MG 45.812"));
		PacienteResponse paciente = pacienteService.cadastrar(Dados.paciente("Ana Beatriz Moreira", "123.456.789-00"));
		consultaService.agendarEspecializada(new ConsultaEspecializadaRequest(paciente.id(), cardiologista.id(), Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(9, 0), "Palpitações"));
		entityManager.flush();
		entityManager.clear();

		assertEquals(1, profissionalService.listarDisponiveisPorEspecialidade(Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(10, 0)).size());
		assertTrue(profissionalService.listarDisponiveisPorEspecialidade(Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(9, 0)).isEmpty());
		assertTrue(profissionalService.listarDisponiveisPorEspecialidade(Especialidade.CARDIOLOGIA, Dados.PROXIMA_QUARTA, LocalTime.of(15, 0)).isEmpty());
	}

	@Test
	void profissionalInexistenteLancaNaoEncontrado() {
		assertThrows(RecursoNaoEncontradoException.class, () -> profissionalService.buscar(9999L));
	}

}
