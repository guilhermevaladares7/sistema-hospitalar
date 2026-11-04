package br.pucminas.sih.service;

import br.pucminas.sih.dto.ConsultaEspecializadaRequest;
import br.pucminas.sih.dto.ConsultaResponse;
import br.pucminas.sih.dto.ConsultaRotinaRequest;
import br.pucminas.sih.dto.EncaminhamentoRequest;
import br.pucminas.sih.dto.EncaminhamentoResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.model.Consulta;
import br.pucminas.sih.model.ConsultaEspecializada;
import br.pucminas.sih.model.ConsultaRotina;
import br.pucminas.sih.model.Encaminhamento;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.model.ProfissionalSaude;
import br.pucminas.sih.repository.ConsultaRepository;
import br.pucminas.sih.repository.ConsultaRotinaRepository;
import br.pucminas.sih.repository.EncaminhamentoRepository;
import br.pucminas.sih.repository.PacienteRepository;
import br.pucminas.sih.repository.ProfissionalSaudeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultaService {

	private final ConsultaRepository consultaRepository;
	private final ConsultaRotinaRepository consultaRotinaRepository;
	private final PacienteRepository pacienteRepository;
	private final ProfissionalSaudeRepository profissionalSaudeRepository;
	private final EncaminhamentoRepository encaminhamentoRepository;

	public ConsultaService(ConsultaRepository consultaRepository, ConsultaRotinaRepository consultaRotinaRepository, PacienteRepository pacienteRepository, ProfissionalSaudeRepository profissionalSaudeRepository, EncaminhamentoRepository encaminhamentoRepository) {
		this.consultaRepository = consultaRepository;
		this.consultaRotinaRepository = consultaRotinaRepository;
		this.pacienteRepository = pacienteRepository;
		this.profissionalSaudeRepository = profissionalSaudeRepository;
		this.encaminhamentoRepository = encaminhamentoRepository;
	}

	@Transactional
	public ConsultaResponse agendarRotina(ConsultaRotinaRequest request) {
		Paciente paciente = pacienteRepository.buscarPorId(request.pacienteId());
		ProfissionalSaude profissional = profissionalSaudeRepository.buscarPorId(request.profissionalId());
		ConsultaRotina consulta = new ConsultaRotina(paciente, profissional, request.data(), request.horario(), request.motivo());
		return agendar(consulta);
	}

	@Transactional
	public ConsultaResponse agendarEspecializada(ConsultaEspecializadaRequest request) {
		Paciente paciente = pacienteRepository.buscarPorId(request.pacienteId());
		ProfissionalSaude profissional = profissionalSaudeRepository.buscarPorId(request.profissionalId());
		ConsultaEspecializada consulta = new ConsultaEspecializada(paciente, profissional, request.especialidade(), request.data(), request.horario(), request.motivo());
		return agendar(consulta);
	}

	@Transactional
	public ConsultaResponse confirmar(Long id) {
		Consulta consulta = consultaRepository.buscarPorId(id);
		consulta.confirmar();
		return ConsultaResponse.de(consulta);
	}

	@Transactional
	public ConsultaResponse iniciar(Long id) {
		Consulta consulta = consultaRepository.buscarPorId(id);
		consulta.iniciar();
		return ConsultaResponse.de(consulta);
	}

	@Transactional
	public ConsultaResponse concluir(Long id, ObservacoesRequest request) {
		Consulta consulta = consultaRepository.buscarPorId(id);
		consulta.concluir(request.observacoes());
		return ConsultaResponse.de(consulta);
	}

	@Transactional
	public ConsultaResponse cancelar(Long id) {
		Consulta consulta = consultaRepository.buscarPorId(id);
		consulta.cancelar();
		return ConsultaResponse.de(consulta);
	}

	@Transactional
	public ConsultaResponse registrarNaoComparecimento(Long id) {
		Consulta consulta = consultaRepository.buscarPorId(id);
		consulta.registrarNaoComparecimento();
		return ConsultaResponse.de(consulta);
	}

	@Transactional
	public EncaminhamentoResponse encaminhar(Long consultaId, EncaminhamentoRequest request) {
		ConsultaRotina consulta = consultaRotinaRepository.buscarPorId(consultaId);
		Encaminhamento encaminhamento = consulta.getProfissional().encaminhar(consulta, request.especialidadeDestino(), request.motivo(), LocalDate.now());
		encaminhamentoRepository.save(encaminhamento);
		return EncaminhamentoResponse.de(encaminhamento);
	}

	public ConsultaResponse buscar(Long id) {
		return ConsultaResponse.de(consultaRepository.buscarPorId(id));
	}

	public List<ConsultaResponse> listar() {
		return converter(consultaRepository.findAll());
	}

	public List<ConsultaResponse> listarPorData(LocalDate data) {
		return converter(consultaRepository.findByData(data));
	}

	private ConsultaResponse agendar(Consulta consulta) {
		consulta.getProfissional().adicionarConsulta(consulta);
		consulta.getPaciente().adicionarConsulta(consulta);
		consultaRepository.save(consulta);
		return ConsultaResponse.de(consulta);
	}

	private List<ConsultaResponse> converter(List<Consulta> consultas) {
		List<ConsultaResponse> respostas = new ArrayList<>();
		for (Consulta consulta : consultas) {
			respostas.add(ConsultaResponse.de(consulta));
		}
		return respostas;
	}

}
