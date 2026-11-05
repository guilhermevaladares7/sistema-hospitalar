package br.pucminas.sih.controller;

import br.pucminas.sih.dto.AltaRequest;
import br.pucminas.sih.dto.InternacaoRequest;
import br.pucminas.sih.dto.InternacaoResponse;
import br.pucminas.sih.service.InternacaoService;
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
@RequestMapping("/internacoes")
public class InternacaoController {

	private final InternacaoService internacaoService;

	public InternacaoController(InternacaoService internacaoService) {
		this.internacaoService = internacaoService;
	}

	@GetMapping
	public ResponseEntity<List<InternacaoResponse>> listar(@RequestParam(required = false) Boolean ativas) {
		if (ativas != null && ativas) {
			return ResponseEntity.ok(internacaoService.listarAtivas());
		} else {
			return ResponseEntity.ok(internacaoService.listar());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<InternacaoResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(internacaoService.buscar(id));
	}

	@PostMapping
	public ResponseEntity<InternacaoResponse> registrar(@RequestBody @Valid InternacaoRequest request, UriComponentsBuilder uriBuilder) {
		InternacaoResponse internacao = internacaoService.registrar(request);
		URI endereco = uriBuilder.path("/internacoes/{id}").buildAndExpand(internacao.id()).toUri();
		return ResponseEntity.created(endereco).body(internacao);
	}

	@PatchMapping("/{id}/alta")
	public ResponseEntity<InternacaoResponse> darAlta(@PathVariable Long id, @RequestBody @Valid AltaRequest request) {
		return ResponseEntity.ok(internacaoService.darAlta(id, request));
	}

}
