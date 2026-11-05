package br.pucminas.sih.controller;

import br.pucminas.sih.dto.HistoricoResponse;
import br.pucminas.sih.dto.PacienteRequest;
import br.pucminas.sih.dto.PacienteResponse;
import br.pucminas.sih.service.PacienteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

	private final PacienteService pacienteService;

	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	@GetMapping
	public ResponseEntity<List<PacienteResponse>> listar(@RequestParam(required = false) String cpf) {
		if (cpf == null) {
			return ResponseEntity.ok(pacienteService.listar());
		} else {
			return ResponseEntity.ok(List.of(pacienteService.buscarPorCpf(cpf)));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<PacienteResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(pacienteService.buscar(id));
	}

	@GetMapping("/{id}/historico")
	public ResponseEntity<HistoricoResponse> buscarHistorico(@PathVariable Long id) {
		return ResponseEntity.ok(pacienteService.buscarHistorico(id));
	}

	@PostMapping
	public ResponseEntity<PacienteResponse> cadastrar(@RequestBody @Valid PacienteRequest request, UriComponentsBuilder uriBuilder) {
		PacienteResponse paciente = pacienteService.cadastrar(request);
		URI endereco = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.id()).toUri();
		return ResponseEntity.created(endereco).body(paciente);
	}

}
