package br.pucminas.sih.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.model.Especialidade;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValidacaoRequestTest {

	private static ValidatorFactory fabrica;
	private static Validator validador;

	@BeforeAll
	static void criarValidador() {
		fabrica = Validation.buildDefaultValidatorFactory();
		validador = fabrica.getValidator();
	}

	@AfterAll
	static void fecharValidador() {
		fabrica.close();
	}

	private Set<String> camposInvalidos(Object request) {
		Set<String> campos = new HashSet<>();
		for (ConstraintViolation<Object> violacao : validador.validate(request)) {
			campos.add(violacao.getPropertyPath().toString());
		}
		return campos;
	}

	private EnderecoRequest enderecoValido() {
		return new EnderecoRequest("Rua dos Timbiras", "1200", null, "Funcionários", "Belo Horizonte", "MG", "30140-060");
	}

	@Test
	void pacienteValidoNaoTemErros() {
		PacienteRequest request = new PacienteRequest("Ana", "123.456.789-00", LocalDate.of(1991, 3, 14), "(31) 98812-3401", "ana@email.com", enderecoValido());

		assertTrue(camposInvalidos(request).isEmpty());
	}

	@Test
	void pacienteComDadosInvalidos() {
		EnderecoRequest endereco = new EnderecoRequest("Rua dos Timbiras", "1200", null, "Funcionários", "Belo Horizonte", "Minas", "30140060");
		PacienteRequest request = new PacienteRequest(" ", "12345678900", LocalDate.now().plusDays(1), "(31) 98812-3401", "email-invalido", endereco);

		assertEquals(Set.of("nome", "cpf", "dataNascimento", "email", "endereco.uf", "endereco.cep"), camposInvalidos(request));
	}

	@Test
	void especialistaExigeEspecialidadeEDisponibilidadesValidas() {
		List<DisponibilidadeRequest> disponibilidades = List.of(new DisponibilidadeRequest(null, LocalTime.of(8, 0), LocalTime.of(12, 0)));
		EspecialistaRequest request = new EspecialistaRequest("Dra. Helena", "CRM-MG 45.812", null, "(31) 3290-1001", "helena@sih.com.br", disponibilidades);

		assertEquals(Set.of("especialidade", "disponibilidades[0].diaSemana"), camposInvalidos(request));
	}

	@Test
	void consultaNoPassadoEhInvalida() {
		ConsultaEspecializadaRequest request = new ConsultaEspecializadaRequest(1L, 2L, Especialidade.CARDIOLOGIA, LocalDate.now().minusDays(1), LocalTime.of(9, 0), "Palpitações");

		assertEquals(Set.of("data"), camposInvalidos(request));
	}

	@Test
	void quartoComValoresInvalidos() {
		EnfermariaRequest enfermaria = new EnfermariaRequest("101", 1, new BigDecimal("-10.00"), false, false, 0);
		UtiRequest uti = new UtiRequest("", 4, new BigDecimal("1000.001"), true, false, 2, true);

		assertEquals(Set.of("valorBaseDiaria", "quantidadeLeitos"), camposInvalidos(enfermaria));
		assertEquals(Set.of("numero", "valorBaseDiaria"), camposInvalidos(uti));
	}

	@Test
	void disponibilidadeExigeTodosOsCampos() {
		assertEquals(Set.of("diaSemana", "horaInicio", "horaFim"), camposInvalidos(new DisponibilidadeRequest(null, null, null)));
		assertTrue(camposInvalidos(new DisponibilidadeRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0))).isEmpty());
	}

}
