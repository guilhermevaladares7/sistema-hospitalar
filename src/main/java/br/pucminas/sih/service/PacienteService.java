package br.pucminas.sih.service;

import br.pucminas.sih.dto.EncaminhamentoResponse;
import br.pucminas.sih.dto.EnderecoRequest;
import br.pucminas.sih.dto.HistoricoResponse;
import br.pucminas.sih.dto.PacienteRequest;
import br.pucminas.sih.dto.PacienteResponse;
import br.pucminas.sih.dto.RegistroHistoricoResponse;
import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.Atendimento;
import br.pucminas.sih.model.Encaminhamento;
import br.pucminas.sih.model.Endereco;
import br.pucminas.sih.model.Paciente;
import br.pucminas.sih.repository.EncaminhamentoRepository;
import br.pucminas.sih.repository.PacienteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacienteService {

	private final PacienteRepository pacienteRepository;
	private final EncaminhamentoRepository encaminhamentoRepository;

	public PacienteService(PacienteRepository pacienteRepository, EncaminhamentoRepository encaminhamentoRepository) {
		this.pacienteRepository = pacienteRepository;
		this.encaminhamentoRepository = encaminhamentoRepository;
	}

	@Transactional
	public PacienteResponse cadastrar(PacienteRequest request) {
		if (pacienteRepository.existsByCpf(request.cpf())) {
			throw new RegraNegocioException("Já existe um paciente cadastrado com o CPF " + request.cpf() + ".");
		}
		EnderecoRequest dadosEndereco = request.endereco();
		Endereco endereco = new Endereco(dadosEndereco.logradouro(), dadosEndereco.numero(), dadosEndereco.complemento(), dadosEndereco.bairro(), dadosEndereco.cidade(), dadosEndereco.uf(), dadosEndereco.cep());
		Paciente paciente = new Paciente(request.nome(), request.cpf(), request.dataNascimento(), request.telefone(), request.email(), endereco);
		pacienteRepository.save(paciente);
		return PacienteResponse.de(paciente);
	}

	public PacienteResponse buscar(Long id) {
		return PacienteResponse.de(pacienteRepository.buscarPorId(id));
	}

	public PacienteResponse buscarPorCpf(String cpf) {
		Optional<Paciente> paciente = pacienteRepository.findByCpf(cpf);
		if (paciente.isEmpty()) {
			throw new RecursoNaoEncontradoException("Nenhum paciente cadastrado com o CPF " + cpf + ".");
		}
		return PacienteResponse.de(paciente.get());
	}

	public List<PacienteResponse> listar() {
		List<PacienteResponse> pacientes = new ArrayList<>();
		for (Paciente paciente : pacienteRepository.findAll()) {
			pacientes.add(PacienteResponse.de(paciente));
		}
		return pacientes;
	}

	public HistoricoResponse buscarHistorico(Long id) {
		Paciente paciente = pacienteRepository.buscarPorId(id);
		List<RegistroHistoricoResponse> atendimentos = new ArrayList<>();
		for (Atendimento atendimento : paciente.getHistorico().getAtendimentos()) {
			atendimentos.add(RegistroHistoricoResponse.de(atendimento));
		}
		List<EncaminhamentoResponse> encaminhamentos = new ArrayList<>();
		for (Encaminhamento encaminhamento : encaminhamentoRepository.findByPaciente(paciente)) {
			encaminhamentos.add(EncaminhamentoResponse.de(encaminhamento));
		}
		return new HistoricoResponse(PacienteResponse.de(paciente), atendimentos, encaminhamentos);
	}

}
