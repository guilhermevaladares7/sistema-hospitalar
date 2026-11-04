package br.pucminas.sih.service;

import br.pucminas.sih.dto.AltaRequest;
import br.pucminas.sih.dto.InternacaoRequest;
import br.pucminas.sih.dto.InternacaoResponse;
import br.pucminas.sih.model.Internacao;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.model.ProfissionalSaude;
import br.pucminas.sih.model.Quarto;
import br.pucminas.sih.repository.InternacaoRepository;
import br.pucminas.sih.repository.PacienteRepository;
import br.pucminas.sih.repository.ProfissionalSaudeRepository;
import br.pucminas.sih.repository.QuartoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InternacaoService {

	private final InternacaoRepository internacaoRepository;
	private final PacienteRepository pacienteRepository;
	private final ProfissionalSaudeRepository profissionalSaudeRepository;
	private final QuartoRepository quartoRepository;

	public InternacaoService(InternacaoRepository internacaoRepository, PacienteRepository pacienteRepository, ProfissionalSaudeRepository profissionalSaudeRepository, QuartoRepository quartoRepository) {
		this.internacaoRepository = internacaoRepository;
		this.pacienteRepository = pacienteRepository;
		this.profissionalSaudeRepository = profissionalSaudeRepository;
		this.quartoRepository = quartoRepository;
	}

	@Transactional
	public InternacaoResponse registrar(InternacaoRequest request) {
		Paciente paciente = pacienteRepository.buscarPorId(request.pacienteId());
		ProfissionalSaude profissional = profissionalSaudeRepository.buscarPorId(request.profissionalId());
		Quarto quarto = quartoRepository.buscarPorId(request.quartoId());
		Internacao internacao = new Internacao(paciente, profissional, quarto, request.dataEntrada(), request.dataPrevistaAlta(), request.comAcompanhante());
		internacao.validarRealizacao();
		quarto.ocupar(internacao);
		paciente.adicionarInternacao(internacao);
		profissional.adicionarInternacao(internacao);
		internacaoRepository.save(internacao);
		return InternacaoResponse.de(internacao);
	}

	@Transactional
	public InternacaoResponse darAlta(Long id, AltaRequest request) {
		Internacao internacao = internacaoRepository.buscarPorId(id);
		internacao.darAlta(request.dataAlta());
		return InternacaoResponse.de(internacao);
	}

	public InternacaoResponse buscar(Long id) {
		return InternacaoResponse.de(internacaoRepository.buscarPorId(id));
	}

	public List<InternacaoResponse> listar() {
		return converter(internacaoRepository.findAll());
	}

	public List<InternacaoResponse> listarAtivas() {
		return converter(internacaoRepository.findByDataEfetivaAltaIsNull());
	}

	private List<InternacaoResponse> converter(List<Internacao> internacoes) {
		List<InternacaoResponse> respostas = new ArrayList<>();
		for (Internacao internacao : internacoes) {
			respostas.add(InternacaoResponse.de(internacao));
		}
		return respostas;
	}

}
