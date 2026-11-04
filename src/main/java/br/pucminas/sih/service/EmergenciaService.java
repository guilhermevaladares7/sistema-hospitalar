package br.pucminas.sih.service;

import br.pucminas.sih.dto.EmergenciaRequest;
import br.pucminas.sih.dto.EmergenciaResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.model.AtendimentoEmergencia;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.model.ProfissionalSaude;
import br.pucminas.sih.model.SituacaoEmergencia;
import br.pucminas.sih.repository.AtendimentoEmergenciaRepository;
import br.pucminas.sih.repository.PacienteRepository;
import br.pucminas.sih.repository.ProfissionalSaudeRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmergenciaService {

	private final AtendimentoEmergenciaRepository atendimentoEmergenciaRepository;
	private final PacienteRepository pacienteRepository;
	private final ProfissionalSaudeRepository profissionalSaudeRepository;

	public EmergenciaService(AtendimentoEmergenciaRepository atendimentoEmergenciaRepository, PacienteRepository pacienteRepository, ProfissionalSaudeRepository profissionalSaudeRepository) {
		this.atendimentoEmergenciaRepository = atendimentoEmergenciaRepository;
		this.pacienteRepository = pacienteRepository;
		this.profissionalSaudeRepository = profissionalSaudeRepository;
	}

	@Transactional
	public EmergenciaResponse abrir(EmergenciaRequest request) {
		Paciente paciente = pacienteRepository.buscarPorId(request.pacienteId());
		ProfissionalSaude profissional = profissionalSaudeRepository.buscarPorId(request.profissionalId());
		AtendimentoEmergencia atendimento = new AtendimentoEmergencia(paciente, profissional, request.prioridade(), LocalDateTime.now());
		paciente.adicionarAtendimentoEmergencia(atendimento);
		atendimentoEmergenciaRepository.save(atendimento);
		return EmergenciaResponse.de(atendimento);
	}

	@Transactional
	public EmergenciaResponse iniciar(Long id) {
		AtendimentoEmergencia atendimento = atendimentoEmergenciaRepository.buscarPorId(id);
		atendimento.iniciar();
		return EmergenciaResponse.de(atendimento);
	}

	@Transactional
	public EmergenciaResponse concluir(Long id, ObservacoesRequest request) {
		AtendimentoEmergencia atendimento = atendimentoEmergenciaRepository.buscarPorId(id);
		atendimento.concluir(request.observacoes());
		return EmergenciaResponse.de(atendimento);
	}

	public EmergenciaResponse buscar(Long id) {
		return EmergenciaResponse.de(atendimentoEmergenciaRepository.buscarPorId(id));
	}

	public List<EmergenciaResponse> listarAguardandoPorPrioridade() {
		List<AtendimentoEmergencia> aguardando = new ArrayList<>(atendimentoEmergenciaRepository.findBySituacao(SituacaoEmergencia.AGUARDANDO_AVALIACAO));
		aguardando.sort(AtendimentoEmergencia.ORDEM_DE_ATENDIMENTO);
		return converter(aguardando);
	}

	public List<EmergenciaResponse> listarPorSituacao(SituacaoEmergencia situacao) {
		return converter(atendimentoEmergenciaRepository.findBySituacao(situacao));
	}

	private List<EmergenciaResponse> converter(List<AtendimentoEmergencia> atendimentos) {
		List<EmergenciaResponse> respostas = new ArrayList<>();
		for (AtendimentoEmergencia atendimento : atendimentos) {
			respostas.add(EmergenciaResponse.de(atendimento));
		}
		return respostas;
	}

}
