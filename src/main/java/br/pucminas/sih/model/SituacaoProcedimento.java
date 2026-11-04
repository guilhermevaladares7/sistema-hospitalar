package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;

public enum SituacaoProcedimento {

	AGENDADO("agendado") {
		@Override
		public boolean podeMudarPara(SituacaoProcedimento nova) {
			if (nova == EM_ANDAMENTO || nova == CANCELADO) {
				return true;
			} else {
				return false;
			}
		}
	},

	EM_ANDAMENTO("em andamento") {
		@Override
		public boolean podeMudarPara(SituacaoProcedimento nova) {
			if (nova == CONCLUIDO) {
				return true;
			} else {
				return false;
			}
		}
	},

	CONCLUIDO("concluído") {
		@Override
		public boolean podeMudarPara(SituacaoProcedimento nova) {
			return false;
		}
	},

	CANCELADO("cancelado") {
		@Override
		public boolean podeMudarPara(SituacaoProcedimento nova) {
			return false;
		}
	};

	private final String descricao;

	SituacaoProcedimento(String descricao) {
		this.descricao = descricao;
	}

	public abstract boolean podeMudarPara(SituacaoProcedimento nova);

	public void validarMudancaPara(SituacaoProcedimento nova) {
		if (!podeMudarPara(nova)) {
			throw new RegraNegocioException("Um procedimento " + descricao + " não pode passar para " + nova.getDescricao() + ".");
		}
	}

	public boolean ocupaHorario() {
		if (this == CANCELADO) {
			return false;
		} else {
			return true;
		}
	}

	public String getDescricao() {
		return descricao;
	}

}
