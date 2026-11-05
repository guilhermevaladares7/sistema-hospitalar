package br.pucminas.sih.controller;

import br.pucminas.sih.dto.CirurgiaoRequest;
import br.pucminas.sih.dto.ClinicoGeralRequest;
import br.pucminas.sih.dto.EspecialistaRequest;
import br.pucminas.sih.dto.ProfissionalResponse;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.service.ProfissionalService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/profissionais")
public class ProfissionalController {

	private final ProfissionalService profissionalService;

	public ProfissionalController(ProfissionalService profissionalService) {
		this.profissionalService = profissionalService;
	}

	@GetMapping
	public ResponseEntity<List<ProfissionalResponse>> listar(
			@RequestParam(required = false) Especialidade especialidade,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horario) {
		if (especialidade != null && data != null && horario != null) {
			return ResponseEntity.ok(profissionalService.listarDisponiveisPorEspecialidade(especialidade, data, horario));
		}
		if (especialidade != null) {
			return ResponseEntity.ok(profissionalService.listarPorEspecialidade(especialidade));
		}
		return ResponseEntity.ok(profissionalService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProfissionalResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(profissionalService.buscar(id));
	}

	@PostMapping("/clinicos-gerais")
	public ResponseEntity<ProfissionalResponse> cadastrarClinicoGeral(@RequestBody @Valid ClinicoGeralRequest request, UriComponentsBuilder uriBuilder) {
		return criado(profissionalService.cadastrarClinicoGeral(request), uriBuilder);
	}

	@PostMapping("/especialistas")
	public ResponseEntity<ProfissionalResponse> cadastrarEspecialista(@RequestBody @Valid EspecialistaRequest request, UriComponentsBuilder uriBuilder) {
		return criado(profissionalService.cadastrarEspecialista(request), uriBuilder);
	}

	@PostMapping("/cirurgioes")
	public ResponseEntity<ProfissionalResponse> cadastrarCirurgiao(@RequestBody @Valid CirurgiaoRequest request, UriComponentsBuilder uriBuilder) {
		return criado(profissionalService.cadastrarCirurgiao(request), uriBuilder);
	}

	private ResponseEntity<ProfissionalResponse> criado(ProfissionalResponse profissional, UriComponentsBuilder uriBuilder) {
		URI endereco = uriBuilder.path("/profissionais/{id}").buildAndExpand(profissional.id()).toUri();
		return ResponseEntity.created(endereco).body(profissional);
	}

}
