package br.pucminas.sih.controller;

import br.pucminas.sih.dto.ConclusaoProcedimentoRequest;
import br.pucminas.sih.dto.InicioProcedimentoRequest;
import br.pucminas.sih.dto.ProcedimentoRequest;
import br.pucminas.sih.dto.ProcedimentoResponse;
import br.pucminas.sih.model.SituacaoProcedimento;
import br.pucminas.sih.service.ProcedimentoService;
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
@RequestMapping("/procedimentos")
public class ProcedimentoController {

	private final ProcedimentoService procedimentoService;

	public ProcedimentoController(ProcedimentoService procedimentoService) {
		this.procedimentoService = procedimentoService;
	}

	@GetMapping
	public ResponseEntity<List<ProcedimentoResponse>> listar(
			@RequestParam(required = false) SituacaoProcedimento situacao,
			@RequestParam(required = false) Long cirurgiaoId) {
		if (cirurgiaoId != null) {
			return ResponseEntity.ok(procedimentoService.listarRealizadosPorCirurgiao(cirurgiaoId));
		}
		if (situacao != null) {
			return ResponseEntity.ok(procedimentoService.listarPorSituacao(situacao));
		}
		return ResponseEntity.ok(procedimentoService.listarPorSituacao(SituacaoProcedimento.AGENDADO));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProcedimentoResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(procedimentoService.buscar(id));
	}

	@PostMapping
	public ResponseEntity<ProcedimentoResponse> agendar(@RequestBody @Valid ProcedimentoRequest request, UriComponentsBuilder uriBuilder) {
		ProcedimentoResponse procedimento = procedimentoService.agendar(request);
		URI endereco = uriBuilder.path("/procedimentos/{id}").buildAndExpand(procedimento.id()).toUri();
		return ResponseEntity.created(endereco).body(procedimento);
	}

	@PatchMapping("/{id}/iniciar")
	public ResponseEntity<ProcedimentoResponse> iniciar(@PathVariable Long id, @RequestBody @Valid InicioProcedimentoRequest request) {
		return ResponseEntity.ok(procedimentoService.iniciar(id, request));
	}

	@PatchMapping("/{id}/concluir")
	public ResponseEntity<ProcedimentoResponse> concluir(@PathVariable Long id, @RequestBody @Valid ConclusaoProcedimentoRequest request) {
		return ResponseEntity.ok(procedimentoService.concluir(id, request));
	}

	@PatchMapping("/{id}/cancelar")
	public ResponseEntity<ProcedimentoResponse> cancelar(@PathVariable Long id) {
		return ResponseEntity.ok(procedimentoService.cancelar(id));
	}

}
