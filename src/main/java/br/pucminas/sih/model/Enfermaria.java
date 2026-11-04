package br.pucminas.sih.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("ENFERMARIA")
public class Enfermaria extends Quarto {

	private static final BigDecimal DESCONTO_POR_LEITO = new BigDecimal("0.05");
	private static final BigDecimal DESCONTO_MAXIMO = new BigDecimal("0.20");

	@Column(name = "quantidade_leitos")
	private int quantidadeLeitos;

	protected Enfermaria() {
	}

	public Enfermaria(String numero, int andar, BigDecimal valorBaseDiaria, boolean possuiArCondicionado, boolean possuiBanheiroPrivativo, int quantidadeLeitos) {
		super(numero, andar, valorBaseDiaria, possuiArCondicionado, possuiBanheiroPrivativo);
		validarQuantidadeLeitos(quantidadeLeitos);
		this.quantidadeLeitos = quantidadeLeitos;
	}

	public BigDecimal getPercentualDesconto() {
		BigDecimal desconto = DESCONTO_POR_LEITO.multiply(BigDecimal.valueOf(quantidadeLeitos - 1));
		if (desconto.compareTo(DESCONTO_MAXIMO) > 0) {
			return DESCONTO_MAXIMO;
		} else {
			return desconto;
		}
	}

	@Override
	public BigDecimal calcularDiaria(boolean comAcompanhante) {
		validarAcompanhante(comAcompanhante);
		BigDecimal fator = BigDecimal.ONE.subtract(getPercentualDesconto());
		return arredondar(getValorBaseDiaria().multiply(fator));
	}

	@Override
	public boolean permiteAcompanhante() {
		return false;
	}

	@Override
	public int getCapacidadeMaxima() {
		return quantidadeLeitos;
	}

	public int getQuantidadeLeitos() {
		return quantidadeLeitos;
	}

}
