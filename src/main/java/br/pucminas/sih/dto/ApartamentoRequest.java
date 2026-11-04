package br.pucminas.sih.dto;

import br.pucminas.sih.model.TipoLeito;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ApartamentoRequest(
		@NotBlank String numero,
		int andar,
		@NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal valorBaseDiaria,
		boolean possuiArCondicionado,
		boolean possuiBanheiroPrivativo,
		@NotNull TipoLeito tipoLeito,
		boolean possuiLeitoAcompanhante) {
}
