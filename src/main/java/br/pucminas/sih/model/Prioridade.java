package br.pucminas.sih.model;

public enum Prioridade {

	BAIXA(1),
	MODERADA(2),
	ALTA(3),
	CRITICA(4);

	private final int nivel;

	Prioridade(int nivel) {
		this.nivel = nivel;
	}

	public boolean ehMaisUrgenteQue(Prioridade outra) {
		if (nivel > outra.nivel) {
			return true;
		} else {
			return false;
		}
	}

	public int getNivel() {
		return nivel;
	}

}
