package br.pucminas.sih.controller;

import br.pucminas.sih.dto.ApartamentoRequest;
import br.pucminas.sih.dto.EnfermariaRequest;
import br.pucminas.sih.dto.QuartoResponse;
import br.pucminas.sih.dto.UtiRequest;
import br.pucminas.sih.service.QuartoService;
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
@RequestMapping("/quartos")
public class QuartoController {

	private final QuartoService quartoService;

	public QuartoController(QuartoService quartoService) {
		this.quartoService = quartoService;
	}

	@GetMapping
	public ResponseEntity<List<QuartoResponse>> listar(@RequestParam(required = false) Boolean disponiveis) {
		if (disponiveis != null && disponiveis) {
			return ResponseEntity.ok(quartoService.listarDisponiveis());
		} else {
			return ResponseEntity.ok(quartoService.listar());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<QuartoResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(quartoService.buscar(id));
	}

	@PostMapping("/enfermarias")
	public ResponseEntity<QuartoResponse> cadastrarEnfermaria(@RequestBody @Valid EnfermariaRequest request, UriComponentsBuilder uriBuilder) {
		return criado(quartoService.cadastrarEnfermaria(request), uriBuilder);
	}

	@PostMapping("/apartamentos")
	public ResponseEntity<QuartoResponse> cadastrarApartamento(@RequestBody @Valid ApartamentoRequest request, UriComponentsBuilder uriBuilder) {
		return criado(quartoService.cadastrarApartamento(request), uriBuilder);
	}

	@PostMapping("/utis")
	public ResponseEntity<QuartoResponse> cadastrarUti(@RequestBody @Valid UtiRequest request, UriComponentsBuilder uriBuilder) {
		return criado(quartoService.cadastrarUti(request), uriBuilder);
	}

	private ResponseEntity<QuartoResponse> criado(QuartoResponse quarto, UriComponentsBuilder uriBuilder) {
		URI endereco = uriBuilder.path("/quartos/{id}").buildAndExpand(quarto.id()).toUri();
		return ResponseEntity.created(endereco).body(quarto);
	}

}
