package br.pucminas.sih.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ClinicoGeralRequest(
		@NotBlank String nome,
		@NotBlank String registroProfissional,
		@NotBlank String telefone,
		@NotBlank @Email String email,
		@NotNull List<@NotNull @Valid DisponibilidadeRequest> disponibilidades) {
}
