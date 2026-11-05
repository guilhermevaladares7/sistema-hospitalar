package br.pucminas.sih.config;

import br.pucminas.sih.dto.ApartamentoRequest;
import br.pucminas.sih.dto.ClinicoGeralRequest;
import br.pucminas.sih.dto.ConsultaEspecializadaRequest;
import br.pucminas.sih.dto.ConsultaRotinaRequest;
import br.pucminas.sih.dto.DisponibilidadeRequest;
import br.pucminas.sih.dto.EmergenciaRequest;
import br.pucminas.sih.dto.EnderecoRequest;
import br.pucminas.sih.dto.EnfermariaRequest;
import br.pucminas.sih.dto.EspecialistaRequest;
import br.pucminas.sih.dto.InternacaoRequest;
import br.pucminas.sih.dto.PacienteRequest;
import br.pucminas.sih.dto.PacienteResponse;
import br.pucminas.sih.dto.ProfissionalResponse;
import br.pucminas.sih.dto.QuartoResponse;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Prioridade;
import br.pucminas.sih.model.TipoLeito;
import br.pucminas.sih.service.ConsultaService;
import br.pucminas.sih.service.EmergenciaService;
import br.pucminas.sih.service.InternacaoService;
import br.pucminas.sih.service.PacienteService;
import br.pucminas.sih.service.ProfissionalService;
import br.pucminas.sih.service.QuartoService;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("h2")
public class DadosExemploRunner implements CommandLineRunner {

	private final PacienteService pacienteService;
	private final ProfissionalService profissionalService;
	private final QuartoService quartoService;
	private final ConsultaService consultaService;
	private final InternacaoService internacaoService;
	private final EmergenciaService emergenciaService;

	public DadosExemploRunner(PacienteService pacienteService, ProfissionalService profissionalService, QuartoService quartoService, ConsultaService consultaService, InternacaoService internacaoService, EmergenciaService emergenciaService) {
		this.pacienteService = pacienteService;
		this.profissionalService = profissionalService;
		this.quartoService = quartoService;
		this.consultaService = consultaService;
		this.internacaoService = internacaoService;
		this.emergenciaService = emergenciaService;
	}

	@Override
	public void run(String... args) {
		if (!pacienteService.listar().isEmpty()) {
			return;
		}

		PacienteResponse ana = cadastrarPaciente("Ana Beatriz Moreira", "123.456.789-00", LocalDate.of(1991, 3, 14), "(31) 98812-3401", "ana.moreira@email.com");
		PacienteResponse carlos = cadastrarPaciente("Carlos Eduardo Lima", "234.567.890-11", LocalDate.of(1985, 7, 2), "(31) 99741-2250", "carlos.lima@email.com");

		LocalDate proximaQuarta = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

		ProfissionalResponse helena = cadastrarEspecialista("Dra. Helena Duarte", "CRM-MG 45.812", Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena.duarte@sih.com.br", DayOfWeek.WEDNESDAY);
		ProfissionalResponse joao = cadastrarClinicoGeral("Dr. João Prado", "CRM-MG 38.004", "(31) 3290-1003", "joao.prado@sih.com.br", DayOfWeek.WEDNESDAY);

		QuartoResponse enfermaria = cadastrarEnfermaria("201", 2, new BigDecimal("180.00"), 4);
		cadastrarApartamento("301", 3, new BigDecimal("520.00"), TipoLeito.ELETRICO);

		agendarConsultaRotina(ana.id(), joao.id(), proximaQuarta, LocalTime.of(9, 0), "Dor de cabeça recorrente");
		agendarConsultaEspecializada(carlos.id(), helena.id(), Especialidade.CARDIOLOGIA, proximaQuarta, LocalTime.of(10, 0), "Avaliação cardiológica");

		internar(carlos.id(), helena.id(), enfermaria.id(), LocalDate.now(), LocalDate.now().plusDays(5), false);

		abrirEmergencia(ana.id(), joao.id(), Prioridade.ALTA);
	}

	private PacienteResponse cadastrarPaciente(String nome, String cpf, LocalDate nascimento, String telefone, String email) {
		EnderecoRequest endereco = new EnderecoRequest("Rua dos Timbiras", "1200", "apto 302", "Funcionários", "Belo Horizonte", "MG", "30140-060");
		PacienteRequest request = new PacienteRequest(nome, cpf, nascimento, telefone, email, endereco);
		return pacienteService.cadastrar(request);
	}

	private ProfissionalResponse cadastrarEspecialista(String nome, String registro, Especialidade especialidade, String telefone, String email, DayOfWeek diaSemana) {
		List<DisponibilidadeRequest> disponibilidades = List.of(new DisponibilidadeRequest(diaSemana, LocalTime.of(8, 0), LocalTime.of(14, 0)));
		EspecialistaRequest request = new EspecialistaRequest(nome, registro, especialidade, telefone, email, disponibilidades);
		return profissionalService.cadastrarEspecialista(request);
	}

	private ProfissionalResponse cadastrarClinicoGeral(String nome, String registro, String telefone, String email, DayOfWeek diaSemana) {
		List<DisponibilidadeRequest> disponibilidades = List.of(new DisponibilidadeRequest(diaSemana, LocalTime.of(8, 0), LocalTime.of(14, 0)));
		ClinicoGeralRequest request = new ClinicoGeralRequest(nome, registro, telefone, email, disponibilidades);
		return profissionalService.cadastrarClinicoGeral(request);
	}

	private QuartoResponse cadastrarEnfermaria(String numero, int andar, BigDecimal valorBaseDiaria, int quantidadeLeitos) {
		EnfermariaRequest request = new EnfermariaRequest(numero, andar, valorBaseDiaria, true, true, quantidadeLeitos);
		return quartoService.cadastrarEnfermaria(request);
	}

	private QuartoResponse cadastrarApartamento(String numero, int andar, BigDecimal valorBaseDiaria, TipoLeito tipoLeito) {
		ApartamentoRequest request = new ApartamentoRequest(numero, andar, valorBaseDiaria, true, true, tipoLeito, true);
		return quartoService.cadastrarApartamento(request);
	}

	private void agendarConsultaRotina(Long pacienteId, Long profissionalId, LocalDate data, LocalTime horario, String motivo) {
		ConsultaRotinaRequest request = new ConsultaRotinaRequest(pacienteId, profissionalId, data, horario, motivo);
		consultaService.agendarRotina(request);
	}

	private void agendarConsultaEspecializada(Long pacienteId, Long profissionalId, Especialidade especialidade, LocalDate data, LocalTime horario, String motivo) {
		ConsultaEspecializadaRequest request = new ConsultaEspecializadaRequest(pacienteId, profissionalId, especialidade, data, horario, motivo);
		consultaService.agendarEspecializada(request);
	}

	private void internar(Long pacienteId, Long profissionalId, Long quartoId, LocalDate entrada, LocalDate previstaAlta, boolean comAcompanhante) {
		InternacaoRequest request = new InternacaoRequest(pacienteId, profissionalId, quartoId, entrada, previstaAlta, comAcompanhante);
		internacaoService.registrar(request);
	}

	private void abrirEmergencia(Long pacienteId, Long profissionalId, Prioridade prioridade) {
		EmergenciaRequest request = new EmergenciaRequest(pacienteId, profissionalId, prioridade);
		emergenciaService.abrir(request);
	}

}
