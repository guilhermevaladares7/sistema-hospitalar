package br.pucminas.sih.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record PacienteRequest(
		@NotBlank String nome,
		@NotBlank @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "deve estar no formato 000.000.000-00") String cpf,
		@NotNull @PastOrPresent LocalDate dataNascimento,
		@NotBlank String telefone,
		@NotBlank @Email String email,
		@NotNull @Valid EnderecoRequest endereco) {
}
