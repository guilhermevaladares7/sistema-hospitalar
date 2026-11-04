package br.pucminas.sih.dto;

import br.pucminas.sih.model.AreaCirurgica;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CirurgiaoRequest(
		@NotBlank String nome,
		@NotBlank String registroProfissional,
		@NotNull AreaCirurgica areaCirurgica,
		@NotBlank String telefone,
		@NotBlank @Email String email,
		@NotNull List<@NotNull @Valid DisponibilidadeRequest> disponibilidades) {
}
