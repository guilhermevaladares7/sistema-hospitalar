package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Quarto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String numero;

	@Column(nullable = false)
	private int andar;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valorBaseDiaria;

	@Column(nullable = false)
	private boolean possuiArCondicionado;

	@Column(nullable = false)
	private boolean possuiBanheiroPrivativo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SituacaoQuarto situacao;

	@OneToMany(mappedBy = "quarto")
	private List<Internacao> internacoes = new ArrayList<>();

	protected Quarto() {
	}

	protected Quarto(String numero, int andar, BigDecimal valorBaseDiaria, boolean possuiArCondicionado, boolean possuiBanheiroPrivativo) {
		if (numero == null || numero.isBlank()) {
			throw new RegraNegocioException("O quarto precisa ter um número.");
		}
		if (valorBaseDiaria == null || valorBaseDiaria.signum() <= 0) {
			throw new RegraNegocioException("O valor base da diária precisa ser maior que zero.");
		}
		this.numero = numero;
		this.andar = andar;
		this.valorBaseDiaria = arredondar(valorBaseDiaria);
		this.possuiArCondicionado = possuiArCondicionado;
		this.possuiBanheiroPrivativo = possuiBanheiroPrivativo;
		this.situacao = SituacaoQuarto.DISPONIVEL;
	}

	public abstract TipoQuarto getTipo();

	public abstract int getCapacidadeMaxima();

	public abstract BigDecimal calcularDiaria(boolean comAcompanhante);

	public abstract boolean permiteAcompanhante();

	protected static BigDecimal arredondar(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP);
	}

	protected static void validarQuantidadeLeitos(int quantidadeLeitos) {
		if (quantidadeLeitos < 1) {
			throw new RegraNegocioException("O quarto precisa ter pelo menos 1 leito.");
		}
	}

	public void validarAcompanhante(boolean comAcompanhante) {
		if (comAcompanhante && !permiteAcompanhante()) {
			throw new RegraNegocioException("O quarto " + numero + " não permite acompanhante.");
		}
	}

	public int getOcupacaoAtual() {
		int ocupacao = 0;
		for (Internacao internacao : internacoes) {
			if (internacao.estaAtiva()) {
				ocupacao++;
			}
		}
		return ocupacao;
	}

	public boolean temVaga() {
		if (getOcupacaoAtual() < getCapacidadeMaxima()) {
			return true;
		} else {
			return false;
		}
	}

	public void ocupar(Internacao internacao) {
		if (!temVaga()) {
			throw new RegraNegocioException("O quarto " + numero + " já atingiu a capacidade máxima.");
		}
		validarAcompanhante(internacao.isComAcompanhante());
		internacoes.add(internacao);
		atualizarSituacao();
	}

	public void liberar(Internacao internacao) {
		if (!internacoes.contains(internacao)) {
			throw new RegraNegocioException("Essa internação não pertence ao quarto " + numero + ".");
		}
		atualizarSituacao();
	}

	private void atualizarSituacao() {
		if (temVaga()) {
			situacao = SituacaoQuarto.DISPONIVEL;
		} else {
			situacao = SituacaoQuarto.OCUPADO;
		}
	}

	public Long getId() {
		return id;
	}

	public String getNumero() {
		return numero;
	}

	public int getAndar() {
		return andar;
	}

	public BigDecimal getValorBaseDiaria() {
		return valorBaseDiaria;
	}

	public boolean isPossuiArCondicionado() {
		return possuiArCondicionado;
	}

	public boolean isPossuiBanheiroPrivativo() {
		return possuiBanheiroPrivativo;
	}

	public SituacaoQuarto getSituacao() {
		return situacao;
	}

	public List<Internacao> getInternacoes() {
		return internacoes;
	}

}
