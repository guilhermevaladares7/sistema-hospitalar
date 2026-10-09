package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Quarto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String numero;

	@Column(nullable = false)
	private int andar;

	@Column(nullable = false)
	private int capacidadeMaxima;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SituacaoQuarto situacao;

	@OneToMany(mappedBy = "quarto")
	private List<Internacao> internacoes = new ArrayList<>();

	protected Quarto() {
	}

	public Quarto(String numero, int andar, int capacidadeMaxima) {
		if (capacidadeMaxima < 1) {
			throw new RegraNegocioException("A capacidade máxima do quarto precisa ser pelo menos 1.");
		}
		this.numero = numero;
		this.andar = andar;
		this.capacidadeMaxima = capacidadeMaxima;
		this.situacao = SituacaoQuarto.DISPONIVEL;
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
		if (getOcupacaoAtual() < capacidadeMaxima) {
			return true;
		} else {
			return false;
		}
	}

	public void ocupar(Internacao internacao) {
		if (!temVaga()) {
			throw new RegraNegocioException("O quarto " + numero + " já atingiu a capacidade máxima.");
		}
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

	public int getCapacidadeMaxima() {
		return capacidadeMaxima;
	}

	public SituacaoQuarto getSituacao() {
		return situacao;
	}

	public List<Internacao> getInternacoes() {
		return internacoes;
	}

}
