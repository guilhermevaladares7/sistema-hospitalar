package br.pucminas.sih.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import br.pucminas.sih.model.ClinicoGeral;
import br.pucminas.sih.model.Consulta;
import br.pucminas.sih.model.ConsultaEspecializada;
import br.pucminas.sih.model.ConsultaRotina;
import br.pucminas.sih.model.Disponibilidade;
import br.pucminas.sih.model.Endereco;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Especialista;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.model.SituacaoConsulta;
import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ConsultaRepositoryTest {

	private static final LocalDate QUARTA = LocalDate.of(2026, 10, 14);

	@Autowired
	private ConsultaRepository consultaRepository;

	@Autowired
	private PacienteRepository pacienteRepository;

	@Autowired
	private ProfissionalSaudeRepository profissionalSaudeRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void salvaERecuperaCadaModalidadeComASituacao() {
		Endereco endereco = new Endereco("Rua dos Timbiras", "1200", null, "Funcionários", "Belo Horizonte", "MG", "30140-060");
		Paciente ana = pacienteRepository.save(new Paciente("Ana Beatriz Moreira", "123.456.789-00", LocalDate.of(1991, 3, 14), "(31) 98812-3401", "ana@email.com", endereco));
		ClinicoGeral rafael = new ClinicoGeral("Dr. Rafael Teixeira", "CRM-MG 38.207", "(31) 3290-1002", "rafael@sih.com.br");
		rafael.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));
		Especialista helena = new Especialista("Dra. Helena Duarte", "CRM-MG 45.812", Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena@sih.com.br");
		helena.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(14, 0)));
		profissionalSaudeRepository.saveAll(List.of(rafael, helena));

		ConsultaRotina rotina = new ConsultaRotina(ana, rafael, QUARTA, LocalTime.of(9, 0), "Check-up");
		rafael.adicionarConsulta(rotina);
		rotina.confirmar();
		ConsultaEspecializada especializada = new ConsultaEspecializada(ana, helena, Especialidade.CARDIOLOGIA, QUARTA, LocalTime.of(10, 0), "Palpitações");
		helena.adicionarConsulta(especializada);
		consultaRepository.saveAll(List.of(rotina, especializada));
		entityManager.flush();
		entityManager.clear();

		Consulta rotinaSalva = consultaRepository.findById(rotina.getId()).orElseThrow();
		Consulta especializadaSalva = consultaRepository.findById(especializada.getId()).orElseThrow();

		assertInstanceOf(ConsultaRotina.class, rotinaSalva);
		assertEquals(SituacaoConsulta.CONFIRMADA, rotinaSalva.getSituacao());
		assertEquals(Especialidade.CARDIOLOGIA, assertInstanceOf(ConsultaEspecializada.class, especializadaSalva).getEspecialidade());
		assertEquals(SituacaoConsulta.AGENDADA, especializadaSalva.getSituacao());
		assertEquals(2, consultaRepository.findByData(QUARTA).size());
	}

}
