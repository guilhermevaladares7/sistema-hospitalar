package br.pucminas.sih.controller;

import br.pucminas.sih.dto.EmergenciaRequest;
import br.pucminas.sih.dto.EmergenciaResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.model.SituacaoEmergencia;
import br.pucminas.sih.service.EmergenciaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/emergencias")
public class EmergenciaController {

	private final EmergenciaService emergenciaService;

	public EmergenciaController(EmergenciaService emergenciaService) {
		this.emergenciaService = emergenciaService;
	}

	@GetMapping
	public ResponseEntity<List<EmergenciaResponse>> listar(
			@RequestParam(required = false) Boolean aguardando,
			@RequestParam(required = false) SituacaoEmergencia situacao) {
		if (aguardando != null && aguardando) {
			return ResponseEntity.ok(emergenciaService.listarAguardandoPorPrioridade());
		}
		if (situacao != null) {
			return ResponseEntity.ok(emergenciaService.listarPorSituacao(situacao));
		}
		return ResponseEntity.ok(emergenciaService.listarAguardandoPorPrioridade());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmergenciaResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(emergenciaService.buscar(id));
	}

	@PostMapping
	public ResponseEntity<EmergenciaResponse> abrir(@RequestBody @Valid EmergenciaRequest request, UriComponentsBuilder uriBuilder) {
		EmergenciaResponse atendimento = emergenciaService.abrir(request);
		URI endereco = uriBuilder.path("/emergencias/{id}").buildAndExpand(atendimento.id()).toUri();
		return ResponseEntity.created(endereco).body(atendimento);
	}

	@PatchMapping("/{id}/iniciar")
	public ResponseEntity<EmergenciaResponse> iniciar(@PathVariable Long id) {
		return ResponseEntity.ok(emergenciaService.iniciar(id));
	}

	@PatchMapping("/{id}/concluir")
	public ResponseEntity<EmergenciaResponse> concluir(@PathVariable Long id, @RequestBody @Valid ObservacoesRequest request) {
		return ResponseEntity.ok(emergenciaService.concluir(id, request));
	}

}
