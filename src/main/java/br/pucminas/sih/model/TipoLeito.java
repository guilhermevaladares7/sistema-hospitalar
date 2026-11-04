package br.pucminas.sih.model;

import java.math.BigDecimal;

public enum TipoLeito {

	PADRAO(new BigDecimal("40.00")),
	ELETRICO(new BigDecimal("90.00"));

	private final BigDecimal adicionalDiaria;

	TipoLeito(BigDecimal adicionalDiaria) {
		this.adicionalDiaria = adicionalDiaria;
	}

	public BigDecimal getAdicionalDiaria() {
		return adicionalDiaria;
	}

}
