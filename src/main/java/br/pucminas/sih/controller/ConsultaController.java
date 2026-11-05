package br.pucminas.sih.controller;

import br.pucminas.sih.dto.ConsultaEspecializadaRequest;
import br.pucminas.sih.dto.ConsultaResponse;
import br.pucminas.sih.dto.ConsultaRotinaRequest;
import br.pucminas.sih.dto.EncaminhamentoRequest;
import br.pucminas.sih.dto.EncaminhamentoResponse;
import br.pucminas.sih.dto.ObservacoesRequest;
import br.pucminas.sih.service.ConsultaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/consultas")
public class ConsultaController {

	private final ConsultaService consultaService;

	public ConsultaController(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}

	@GetMapping
	public ResponseEntity<List<ConsultaResponse>> listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
		if (data == null) {
			return ResponseEntity.ok(consultaService.listar());
		} else {
			return ResponseEntity.ok(consultaService.listarPorData(data));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ConsultaResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(consultaService.buscar(id));
	}

	@PostMapping("/rotina")
	public ResponseEntity<ConsultaResponse> agendarRotina(@RequestBody @Valid ConsultaRotinaRequest request, UriComponentsBuilder uriBuilder) {
		return criada(consultaService.agendarRotina(request), uriBuilder);
	}

	@PostMapping("/especializada")
	public ResponseEntity<ConsultaResponse> agendarEspecializada(@RequestBody @Valid ConsultaEspecializadaRequest request, UriComponentsBuilder uriBuilder) {
		return criada(consultaService.agendarEspecializada(request), uriBuilder);
	}

	@PatchMapping("/{id}/confirmar")
	public ResponseEntity<ConsultaResponse> confirmar(@PathVariable Long id) {
		return ResponseEntity.ok(consultaService.confirmar(id));
	}

	@PatchMapping("/{id}/iniciar")
	public ResponseEntity<ConsultaResponse> iniciar(@PathVariable Long id) {
		return ResponseEntity.ok(consultaService.iniciar(id));
	}

	@PatchMapping("/{id}/concluir")
	public ResponseEntity<ConsultaResponse> concluir(@PathVariable Long id, @RequestBody @Valid ObservacoesRequest request) {
		return ResponseEntity.ok(consultaService.concluir(id, request));
	}

	@PatchMapping("/{id}/cancelar")
	public ResponseEntity<ConsultaResponse> cancelar(@PathVariable Long id) {
		return ResponseEntity.ok(consultaService.cancelar(id));
	}

	@PatchMapping("/{id}/nao-compareceu")
	public ResponseEntity<ConsultaResponse> registrarNaoComparecimento(@PathVariable Long id) {
		return ResponseEntity.ok(consultaService.registrarNaoComparecimento(id));
	}

	@PostMapping("/{id}/encaminhamentos")
	public ResponseEntity<EncaminhamentoResponse> encaminhar(@PathVariable Long id, @RequestBody @Valid EncaminhamentoRequest request) {
		EncaminhamentoResponse encaminhamento = consultaService.encaminhar(id, request);
		return ResponseEntity.ok(encaminhamento);
	}

	private ResponseEntity<ConsultaResponse> criada(ConsultaResponse consulta, UriComponentsBuilder uriBuilder) {
		URI endereco = uriBuilder.path("/consultas/{id}").buildAndExpand(consulta.id()).toUri();
		return ResponseEntity.created(endereco).body(consulta);
	}

}
