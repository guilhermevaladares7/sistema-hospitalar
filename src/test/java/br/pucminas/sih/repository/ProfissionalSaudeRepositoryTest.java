package br.pucminas.sih.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.model.AreaCirurgica;
import br.pucminas.sih.model.Cirurgiao;
import br.pucminas.sih.model.ClinicoGeral;
import br.pucminas.sih.model.Disponibilidade;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Especialista;
import br.pucminas.sih.model.ProfissionalSaude;
import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProfissionalSaudeRepositoryTest {

	@Autowired
	private ProfissionalSaudeRepository profissionalSaudeRepository;

	@Autowired
	private EspecialistaRepository especialistaRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void salvaERecuperaCadaCategoriaComOTipoCorreto() {
		ClinicoGeral rafael = new ClinicoGeral("Dr. Rafael Teixeira", "CRM-MG 38.207", "(31) 3290-1002", "rafael@sih.com.br");
		rafael.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));
		Especialista helena = new Especialista("Dra. Helena Duarte", "CRM-MG 45.812", Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena@sih.com.br");
		Cirurgiao marcos = new Cirurgiao("Dr. Marcos Vinícius Prado", "CRM-MG 51.334", AreaCirurgica.ORTOPEDICA, "(31) 3290-1003", "marcos@sih.com.br");
		profissionalSaudeRepository.saveAll(List.of(rafael, helena, marcos));
		entityManager.flush();
		entityManager.clear();

		ProfissionalSaude clinico = profissionalSaudeRepository.findById(rafael.getId()).orElseThrow();
		ProfissionalSaude especialista = profissionalSaudeRepository.findById(helena.getId()).orElseThrow();
		ProfissionalSaude cirurgiao = profissionalSaudeRepository.findById(marcos.getId()).orElseThrow();

		assertInstanceOf(ClinicoGeral.class, clinico);
		assertEquals(1, clinico.getDisponibilidades().size());
		assertEquals(Especialidade.CARDIOLOGIA, assertInstanceOf(Especialista.class, especialista).getEspecialidade());
		assertEquals(AreaCirurgica.ORTOPEDICA, assertInstanceOf(Cirurgiao.class, cirurgiao).getAreaCirurgica());
		assertTrue(profissionalSaudeRepository.existsByRegistroProfissional("CRM-MG 51.334"));
	}

	@Test
	void buscaEspecialistasPorArea() {
		Especialista helena = new Especialista("Dra. Helena Duarte", "CRM-MG 45.812", Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena@sih.com.br");
		Especialista camila = new Especialista("Dra. Camila Fontes", "CRM-MG 40.118", Especialidade.NEUROLOGIA, "(31) 3290-1004", "camila@sih.com.br");
		ClinicoGeral rafael = new ClinicoGeral("Dr. Rafael Teixeira", "CRM-MG 38.207", "(31) 3290-1002", "rafael@sih.com.br");
		profissionalSaudeRepository.saveAll(List.of(helena, camila, rafael));

		List<Especialista> cardiologistas = especialistaRepository.findByEspecialidade(Especialidade.CARDIOLOGIA);

		assertEquals(1, cardiologistas.size());
		assertEquals("Dra. Helena Duarte", cardiologistas.get(0).getNome());
	}

}
