package br.pucminas.sih.dto;

import br.pucminas.sih.model.TipoCirurgia;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ProcedimentoRequest(
		@NotNull Long pacienteId,
		@NotNull Long cirurgiaoId,
		@NotBlank String descricao,
		@NotNull TipoCirurgia tipoCirurgia,
		@NotNull @FutureOrPresent LocalDateTime dataHoraPrevista,
		@Size(max = 2000) String observacoesPreOperatorias) {
}
