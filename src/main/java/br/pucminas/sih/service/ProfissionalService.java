package br.pucminas.sih.service;

import br.pucminas.sih.dto.CirurgiaoRequest;
import br.pucminas.sih.dto.ClinicoGeralRequest;
import br.pucminas.sih.dto.DisponibilidadeRequest;
import br.pucminas.sih.dto.EspecialistaRequest;
import br.pucminas.sih.dto.ProfissionalResponse;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.Cirurgiao;
import br.pucminas.sih.model.ClinicoGeral;
import br.pucminas.sih.model.Disponibilidade;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Especialista;
import br.pucminas.sih.model.ProfissionalSaude;
import br.pucminas.sih.repository.EspecialistaRepository;
import br.pucminas.sih.repository.ProfissionalSaudeRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfissionalService {

	private final ProfissionalSaudeRepository profissionalSaudeRepository;
	private final EspecialistaRepository especialistaRepository;

	public ProfissionalService(ProfissionalSaudeRepository profissionalSaudeRepository, EspecialistaRepository especialistaRepository) {
		this.profissionalSaudeRepository = profissionalSaudeRepository;
		this.especialistaRepository = especialistaRepository;
	}

	@Transactional
	public ProfissionalResponse cadastrarClinicoGeral(ClinicoGeralRequest request) {
		validarRegistroUnico(request.registroProfissional());
		ClinicoGeral clinico = new ClinicoGeral(request.nome(), request.registroProfissional(), request.telefone(), request.email());
		return salvar(clinico, request.disponibilidades());
	}

	@Transactional
	public ProfissionalResponse cadastrarEspecialista(EspecialistaRequest request) {
		validarRegistroUnico(request.registroProfissional());
		Especialista especialista = new Especialista(request.nome(), request.registroProfissional(), request.especialidade(), request.telefone(), request.email());
		return salvar(especialista, request.disponibilidades());
	}

	@Transactional
	public ProfissionalResponse cadastrarCirurgiao(CirurgiaoRequest request) {
		validarRegistroUnico(request.registroProfissional());
		Cirurgiao cirurgiao = new Cirurgiao(request.nome(), request.registroProfissional(), request.areaCirurgica(), request.telefone(), request.email());
		return salvar(cirurgiao, request.disponibilidades());
	}

	public ProfissionalResponse buscar(Long id) {
		return ProfissionalResponse.de(profissionalSaudeRepository.buscarPorId(id));
	}

	public List<ProfissionalResponse> listar() {
		List<ProfissionalResponse> profissionais = new ArrayList<>();
		for (ProfissionalSaude profissional : profissionalSaudeRepository.findAll()) {
			profissionais.add(ProfissionalResponse.de(profissional));
		}
		return profissionais;
	}

	public List<ProfissionalResponse> listarPorEspecialidade(Especialidade especialidade) {
		List<ProfissionalResponse> especialistas = new ArrayList<>();
		for (Especialista especialista : especialistaRepository.findByEspecialidade(especialidade)) {
			especialistas.add(ProfissionalResponse.de(especialista));
		}
		return especialistas;
	}

	public List<ProfissionalResponse> listarDisponiveisPorEspecialidade(Especialidade especialidade, LocalDate data, LocalTime horario) {
		List<ProfissionalResponse> disponiveis = new ArrayList<>();
		for (Especialista especialista : especialistaRepository.findByEspecialidade(especialidade)) {
			if (especialista.estaDisponivel(data, horario)) {
				disponiveis.add(ProfissionalResponse.de(especialista));
			}
		}
		return disponiveis;
	}

	private void validarRegistroUnico(String registroProfissional) {
		if (profissionalSaudeRepository.existsByRegistroProfissional(registroProfissional)) {
			throw new RegraNegocioException("Já existe um profissional cadastrado com o registro " + registroProfissional + ".");
		}
	}

	private ProfissionalResponse salvar(ProfissionalSaude profissional, List<DisponibilidadeRequest> disponibilidades) {
		for (DisponibilidadeRequest disponibilidade : disponibilidades) {
			profissional.adicionarDisponibilidade(new Disponibilidade(disponibilidade.diaSemana(), disponibilidade.horaInicio(), disponibilidade.horaFim()));
		}
		profissionalSaudeRepository.save(profissional);
		return ProfissionalResponse.de(profissional);
	}

}
