package br.pucminas.sih.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("UTI")
public class Uti extends Quarto {

	private static final BigDecimal ACRESCIMO_UTI = new BigDecimal("0.50");
	private static final BigDecimal ACRESCIMO_ISOLAMENTO = new BigDecimal("0.20");

	@Column(name = "quantidade_leitos")
	private int quantidadeLeitos;

	@Column(name = "possui_isolamento")
	private boolean possuiIsolamento;

	protected Uti() {
	}

	public Uti(String numero, int andar, BigDecimal valorBaseDiaria, boolean possuiArCondicionado, boolean possuiBanheiroPrivativo, int quantidadeLeitos, boolean possuiIsolamento) {
		super(numero, andar, valorBaseDiaria, possuiArCondicionado, possuiBanheiroPrivativo);
		validarQuantidadeLeitos(quantidadeLeitos);
		this.quantidadeLeitos = quantidadeLeitos;
		this.possuiIsolamento = possuiIsolamento;
	}

	@Override
	public TipoQuarto getTipo() {
		return TipoQuarto.UTI;
	}

	@Override
	public BigDecimal calcularDiaria(boolean comAcompanhante) {
		validarAcompanhante(comAcompanhante);
		BigDecimal fator = BigDecimal.ONE.add(ACRESCIMO_UTI);
		if (possuiIsolamento) {
			fator = fator.add(ACRESCIMO_ISOLAMENTO);
		}
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

	public boolean isPossuiIsolamento() {
		return possuiIsolamento;
	}

}
