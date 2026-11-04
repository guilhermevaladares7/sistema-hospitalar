package br.pucminas.sih.dto;

import br.pucminas.sih.model.Especialidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EspecialistaRequest(
		@NotBlank String nome,
		@NotBlank String registroProfissional,
		@NotNull Especialidade especialidade,
		@NotBlank String telefone,
		@NotBlank @Email String email,
		@NotNull List<@NotNull @Valid DisponibilidadeRequest> disponibilidades) {
}
