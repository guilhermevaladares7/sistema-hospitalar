package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("APARTAMENTO")
public class Apartamento extends Quarto {

	private static final BigDecimal ADICIONAL_ACOMPANHANTE = new BigDecimal("80.00");

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_leito")
	private TipoLeito tipoLeito;

	@Column(name = "possui_leito_acompanhante")
	private boolean possuiLeitoAcompanhante;

	protected Apartamento() {
	}

	public Apartamento(String numero, int andar, BigDecimal valorBaseDiaria, boolean possuiArCondicionado, boolean possuiBanheiroPrivativo, TipoLeito tipoLeito, boolean possuiLeitoAcompanhante) {
		super(numero, andar, valorBaseDiaria, possuiArCondicionado, possuiBanheiroPrivativo);
		if (tipoLeito == null) {
			throw new RegraNegocioException("O apartamento precisa ter um tipo de leito.");
		}
		this.tipoLeito = tipoLeito;
		this.possuiLeitoAcompanhante = possuiLeitoAcompanhante;
	}

	@Override
	public TipoQuarto getTipo() {
		return TipoQuarto.APARTAMENTO;
	}

	@Override
	public BigDecimal calcularDiaria(boolean comAcompanhante) {
		validarAcompanhante(comAcompanhante);
		BigDecimal diaria = getValorBaseDiaria().add(tipoLeito.getAdicionalDiaria());
		if (comAcompanhante) {
			diaria = diaria.add(ADICIONAL_ACOMPANHANTE);
		}
		return arredondar(diaria);
	}

	@Override
	public boolean permiteAcompanhante() {
		return possuiLeitoAcompanhante;
	}

	@Override
	public int getCapacidadeMaxima() {
		return 1;
	}

	public TipoLeito getTipoLeito() {
		return tipoLeito;
	}

	public boolean isPossuiLeitoAcompanhante() {
		return possuiLeitoAcompanhante;
	}

}
