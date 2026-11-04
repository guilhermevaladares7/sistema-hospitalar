package br.pucminas.sih.service;

import br.pucminas.sih.dto.ConclusaoProcedimentoRequest;
import br.pucminas.sih.dto.InicioProcedimentoRequest;
import br.pucminas.sih.dto.ProcedimentoRequest;
import br.pucminas.sih.dto.ProcedimentoResponse;
import br.pucminas.sih.model.Cirurgiao;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.model.ProcedimentoCirurgico;
import br.pucminas.sih.model.ProfissionalSaude;
import br.pucminas.sih.model.SituacaoProcedimento;
import br.pucminas.sih.repository.CirurgiaoRepository;
import br.pucminas.sih.repository.PacienteRepository;
import br.pucminas.sih.repository.ProcedimentoCirurgicoRepository;
import br.pucminas.sih.repository.ProfissionalSaudeRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProcedimentoService {

	private final ProcedimentoCirurgicoRepository procedimentoCirurgicoRepository;
	private final PacienteRepository pacienteRepository;
	private final ProfissionalSaudeRepository profissionalSaudeRepository;
	private final CirurgiaoRepository cirurgiaoRepository;

	public ProcedimentoService(ProcedimentoCirurgicoRepository procedimentoCirurgicoRepository, PacienteRepository pacienteRepository, ProfissionalSaudeRepository profissionalSaudeRepository, CirurgiaoRepository cirurgiaoRepository) {
		this.procedimentoCirurgicoRepository = procedimentoCirurgicoRepository;
		this.pacienteRepository = pacienteRepository;
		this.profissionalSaudeRepository = profissionalSaudeRepository;
		this.cirurgiaoRepository = cirurgiaoRepository;
	}

	@Transactional
	public ProcedimentoResponse agendar(ProcedimentoRequest request) {
		Paciente paciente = pacienteRepository.buscarPorId(request.pacienteId());
		ProfissionalSaude cirurgiao = profissionalSaudeRepository.buscarPorId(request.cirurgiaoId());
		ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico(paciente, cirurgiao, request.descricao(), request.tipoCirurgia(), request.dataHoraPrevista(), request.observacoesPreOperatorias());
		cirurgiao.adicionarProcedimento(procedimento);
		paciente.adicionarProcedimento(procedimento);
		procedimentoCirurgicoRepository.save(procedimento);
		return ProcedimentoResponse.de(procedimento);
	}

	@Transactional
	public ProcedimentoResponse iniciar(Long id, InicioProcedimentoRequest request) {
		ProcedimentoCirurgico procedimento = procedimentoCirurgicoRepository.buscarPorId(id);
		procedimento.iniciar(request.dataHoraInicio());
		return ProcedimentoResponse.de(procedimento);
	}

	@Transactional
	public ProcedimentoResponse concluir(Long id, ConclusaoProcedimentoRequest request) {
		ProcedimentoCirurgico procedimento = procedimentoCirurgicoRepository.buscarPorId(id);
		procedimento.concluir(request.dataHoraFim(), request.observacoesPosOperatorias());
		return ProcedimentoResponse.de(procedimento);
	}

	@Transactional
	public ProcedimentoResponse cancelar(Long id) {
		ProcedimentoCirurgico procedimento = procedimentoCirurgicoRepository.buscarPorId(id);
		procedimento.cancelar();
		return ProcedimentoResponse.de(procedimento);
	}

	public ProcedimentoResponse buscar(Long id) {
		return ProcedimentoResponse.de(procedimentoCirurgicoRepository.buscarPorId(id));
	}

	public List<ProcedimentoResponse> listarPorSituacao(SituacaoProcedimento situacao) {
		return converter(procedimentoCirurgicoRepository.findBySituacao(situacao));
	}

	public List<ProcedimentoResponse> listarRealizadosPorCirurgiao(Long cirurgiaoId) {
		Cirurgiao cirurgiao = cirurgiaoRepository.buscarPorId(cirurgiaoId);
		return converter(cirurgiao.getProcedimentosRealizados());
	}

	private List<ProcedimentoResponse> converter(List<ProcedimentoCirurgico> procedimentos) {
		List<ProcedimentoResponse> respostas = new ArrayList<>();
		for (ProcedimentoCirurgico procedimento : procedimentos) {
			respostas.add(ProcedimentoResponse.de(procedimento));
		}
		return respostas;
	}

}
