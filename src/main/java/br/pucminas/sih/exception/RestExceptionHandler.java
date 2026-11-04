package br.pucminas.sih.exception;

import br.pucminas.sih.dto.ErroResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(RegraNegocioException.class)
	public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException excecao) {
		return responder(HttpStatus.BAD_REQUEST, excecao.getMessage(), List.of());
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {
		return responder(HttpStatus.NOT_FOUND, excecao.getMessage(), List.of());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErroResponse> tratarViolacaoDeIntegridade(DataIntegrityViolationException excecao) {
		List<String> detalhes = new ArrayList<>();
		Throwable causa = excecao.getMostSpecificCause();
		if (causa != null && causa.getMessage() != null) {
			detalhes.add(causa.getMessage());
		}
		return responder(HttpStatus.CONFLICT, "A operação viola uma restrição de integridade dos dados.", detalhes);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResponse> tratarCamposInvalidos(MethodArgumentNotValidException excecao) {
		List<String> detalhes = new ArrayList<>();
		for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
			detalhes.add(erro.getField() + ": " + erro.getDefaultMessage());
		}
		for (ObjectError erro : excecao.getBindingResult().getGlobalErrors()) {
			detalhes.add(erro.getObjectName() + ": " + erro.getDefaultMessage());
		}
		return responder(HttpStatus.BAD_REQUEST, "Os dados enviados são inválidos.", detalhes);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErroResponse> tratarParametrosInvalidos(HandlerMethodValidationException excecao) {
		List<String> detalhes = new ArrayList<>();
		for (MessageSourceResolvable erro : excecao.getAllErrors()) {
			detalhes.add(erro.getDefaultMessage());
		}
		return responder(HttpStatus.BAD_REQUEST, "Os parâmetros enviados são inválidos.", detalhes);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErroResponse> tratarRestricoesVioladas(ConstraintViolationException excecao) {
		List<String> detalhes = new ArrayList<>();
		for (ConstraintViolation<?> violacao : excecao.getConstraintViolations()) {
			detalhes.add(violacao.getPropertyPath() + ": " + violacao.getMessage());
		}
		return responder(HttpStatus.BAD_REQUEST, "Os dados enviados são inválidos.", detalhes);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResponse> tratarCorpoIlegivel(HttpMessageNotReadableException excecao) {
		return responder(HttpStatus.BAD_REQUEST, "O corpo da requisição está mal formatado ou tem valores inválidos.", List.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErroResponse> tratarTipoInvalido(MethodArgumentTypeMismatchException excecao) {
		return responder(HttpStatus.BAD_REQUEST, "O parâmetro " + excecao.getName() + " tem um valor inválido.", List.of());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErroResponse> tratarParametroAusente(MissingServletRequestParameterException excecao) {
		return responder(HttpStatus.BAD_REQUEST, "O parâmetro " + excecao.getParameterName() + " é obrigatório.", List.of());
	}

	private ResponseEntity<ErroResponse> responder(HttpStatus status, String mensagem, List<String> detalhes) {
		ErroResponse corpo = new ErroResponse(status.value(), mensagem, detalhes);
		return ResponseEntity.status(status).body(corpo);
	}

}
