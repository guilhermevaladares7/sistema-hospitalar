package br.pucminas.sih.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UtiRequest(
		@NotBlank String numero,
		int andar,
		@NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal valorBaseDiaria,
		boolean possuiArCondicionado,
		boolean possuiBanheiroPrivativo,
		@Min(1) int quantidadeLeitos,
		boolean possuiIsolamento) {
}
