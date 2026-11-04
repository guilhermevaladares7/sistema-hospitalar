package br.pucminas.sih.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.dto.ErroResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RestExceptionHandlerTest {

	private final RestExceptionHandler handler = new RestExceptionHandler();

	@Test
	void regraDeNegocioViraBadRequest() {
		ResponseEntity<ErroResponse> resposta = handler.tratarRegraNegocio(new RegraNegocioException("O quarto 101 não permite acompanhante."));

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals(400, resposta.getBody().status());
		assertEquals("O quarto 101 não permite acompanhante.", resposta.getBody().mensagem());
		assertEquals(List.of(), resposta.getBody().detalhes());
	}

	@Test
	void recursoNaoEncontradoViraNotFound() {
		ResponseEntity<ErroResponse> resposta = handler.tratarRecursoNaoEncontrado(new RecursoNaoEncontradoException("O paciente 7 não foi encontrado."));

		assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
		assertEquals(404, resposta.getBody().status());
		assertEquals("O paciente 7 não foi encontrado.", resposta.getBody().mensagem());
	}

	@Test
	void violacaoDeIntegridadeViraConflict() {
		DataIntegrityViolationException excecao = new DataIntegrityViolationException("erro", new IllegalStateException("Unique index violation: CPF"));

		ResponseEntity<ErroResponse> resposta = handler.tratarViolacaoDeIntegridade(excecao);

		assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
		assertEquals(409, resposta.getBody().status());
		assertTrue(resposta.getBody().detalhes().get(0).contains("CPF"));
	}

}
