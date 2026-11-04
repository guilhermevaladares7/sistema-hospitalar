package br.pucminas.sih.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.pucminas.sih.model.AreaCirurgica;
import br.pucminas.sih.model.AtendimentoEmergencia;
import br.pucminas.sih.model.Cirurgiao;
import br.pucminas.sih.model.ClinicoGeral;
import br.pucminas.sih.model.ConsultaRotina;
import br.pucminas.sih.model.Disponibilidade;
import br.pucminas.sih.model.Encaminhamento;
import br.pucminas.sih.model.Endereco;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.model.Prioridade;
import br.pucminas.sih.model.ProcedimentoCirurgico;
import br.pucminas.sih.model.SituacaoEmergencia;
import br.pucminas.sih.model.SituacaoProcedimento;
import br.pucminas.sih.model.TipoCirurgia;
import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AtendimentoRepositoryTest {

	private static final LocalDateTime PREVISTO = LocalDateTime.of(2026, 10, 20, 7, 0);

	@Autowired
	private PacienteRepository pacienteRepository;

	@Autowired
	private ProfissionalSaudeRepository profissionalSaudeRepository;

	@Autowired
	private AtendimentoEmergenciaRepository atendimentoEmergenciaRepository;

	@Autowired
	private ProcedimentoCirurgicoRepository procedimentoCirurgicoRepository;

	@Autowired
	private ConsultaRepository consultaRepository;

	@Autowired
	private EncaminhamentoRepository encaminhamentoRepository;

	@Autowired
	private EntityManager entityManager;

	private Paciente ana;

	@BeforeEach
	void cadastrarPaciente() {
		Endereco endereco = new Endereco("Rua dos Timbiras", "1200", null, "Funcionários", "Belo Horizonte", "MG", "30140-060");
		ana = pacienteRepository.save(new Paciente("Ana Beatriz Moreira", "123.456.789-00", LocalDate.of(1991, 3, 14), "(31) 98812-3401", "ana@email.com", endereco));
	}

	private void sincronizar() {
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void buscaEmergenciasPorSituacao() {
		ClinicoGeral rafael = profissionalSaudeRepository.save(new ClinicoGeral("Dr. Rafael Teixeira", "CRM-MG 38.207", "(31) 3290-1002", "rafael@sih.com.br"));
		AtendimentoEmergencia aguardando = new AtendimentoEmergencia(ana, rafael, Prioridade.CRITICA, PREVISTO);
		AtendimentoEmergencia iniciado = new AtendimentoEmergencia(ana, rafael, Prioridade.BAIXA, PREVISTO.plusMinutes(5));
		iniciado.iniciar();
		atendimentoEmergenciaRepository.saveAll(List.of(aguardando, iniciado));
		sincronizar();

		List<AtendimentoEmergencia> naFila = atendimentoEmergenciaRepository.findBySituacao(SituacaoEmergencia.AGUARDANDO_AVALIACAO);

		assertEquals(1, naFila.size());
		assertEquals(Prioridade.CRITICA, naFila.get(0).getPrioridade());
	}

	@Test
	void procedimentoConcluidoApareceNosRealizadosDoCirurgiaoAoRecarregar() {
		Cirurgiao marcos = profissionalSaudeRepository.save(new Cirurgiao("Dr. Marcos Vinícius Prado", "CRM-MG 51.334", AreaCirurgica.ORTOPEDICA, "(31) 3290-1003", "marcos@sih.com.br"));
		ProcedimentoCirurgico realizado = new ProcedimentoCirurgico(ana, marcos, "Artroscopia de joelho", TipoCirurgia.ELETIVA, PREVISTO, null);
		ProcedimentoCirurgico agendado = new ProcedimentoCirurgico(ana, marcos, "Retirada de pino", TipoCirurgia.ELETIVA, PREVISTO.plusDays(30), null);
		marcos.adicionarProcedimento(realizado);
		marcos.adicionarProcedimento(agendado);
		realizado.iniciar(PREVISTO);
		realizado.concluir(PREVISTO.plusHours(2), "Sem intercorrências");
		procedimentoCirurgicoRepository.saveAll(List.of(realizado, agendado));
		sincronizar();

		Cirurgiao recarregado = (Cirurgiao) profissionalSaudeRepository.findById(marcos.getId()).orElseThrow();

		assertEquals(2, recarregado.getProcedimentos().size());
		assertEquals(1, recarregado.getProcedimentosRealizados().size());
		assertEquals(PREVISTO.plusHours(2), recarregado.getProcedimentosRealizados().get(0).getDataHoraFim());
		assertEquals(1, procedimentoCirurgicoRepository.findBySituacao(SituacaoProcedimento.AGENDADO).size());
	}

	@Test
	void salvaEncaminhamentoEBuscaPorPaciente() {
		ClinicoGeral rafael = new ClinicoGeral("Dr. Rafael Teixeira", "CRM-MG 38.207", "(31) 3290-1002", "rafael@sih.com.br");
		rafael.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));
		profissionalSaudeRepository.save(rafael);
		ConsultaRotina consulta = new ConsultaRotina(ana, rafael, LocalDate.of(2026, 10, 14), LocalTime.of(9, 0), "Check-up");
		rafael.adicionarConsulta(consulta);
		consulta.confirmar();
		consulta.iniciar();
		consulta.concluir("Sopro cardíaco");
		consultaRepository.save(consulta);
		encaminhamentoRepository.save(rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Investigar sopro", LocalDate.of(2026, 10, 14)));
		sincronizar();

		List<Encaminhamento> encaminhamentos = encaminhamentoRepository.findByPaciente(ana);

		assertEquals(1, encaminhamentos.size());
		assertEquals(Especialidade.CARDIOLOGIA, encaminhamentos.get(0).getEspecialidadeDestino());
		assertEquals(consulta.getId(), encaminhamentos.get(0).getConsultaOrigem().getId());
	}

}
