package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;

public enum SituacaoConsulta {

	AGENDADA("agendada") {
		@Override
		public boolean podeMudarPara(SituacaoConsulta nova) {
			if (nova == CONFIRMADA || nova == CANCELADA || nova == NAO_COMPARECEU) {
				return true;
			} else {
				return false;
			}
		}
	},

	CONFIRMADA("confirmada") {
		@Override
		public boolean podeMudarPara(SituacaoConsulta nova) {
			if (nova == EM_ATENDIMENTO || nova == CANCELADA || nova == NAO_COMPARECEU) {
				return true;
			} else {
				return false;
			}
		}
	},

	EM_ATENDIMENTO("em atendimento") {
		@Override
		public boolean podeMudarPara(SituacaoConsulta nova) {
			if (nova == CONCLUIDA) {
				return true;
			} else {
				return false;
			}
		}
	},

	CONCLUIDA("concluída") {
		@Override
		public boolean podeMudarPara(SituacaoConsulta nova) {
			return false;
		}
	},

	CANCELADA("cancelada") {
		@Override
		public boolean podeMudarPara(SituacaoConsulta nova) {
			return false;
		}
	},

	NAO_COMPARECEU("não compareceu") {
		@Override
		public boolean podeMudarPara(SituacaoConsulta nova) {
			return false;
		}
	};

	private final String descricao;

	SituacaoConsulta(String descricao) {
		this.descricao = descricao;
	}

	public abstract boolean podeMudarPara(SituacaoConsulta nova);

	public void validarMudancaPara(SituacaoConsulta nova) {
		if (!podeMudarPara(nova)) {
			throw new RegraNegocioException("Uma consulta " + descricao + " não pode passar para " + nova.getDescricao() + ".");
		}
	}

	public boolean ehFinal() {
		for (SituacaoConsulta situacao : values()) {
			if (podeMudarPara(situacao)) {
				return false;
			}
		}
		return true;
	}

	public boolean ocupaHorario() {
		if (this == CANCELADA || this == NAO_COMPARECEU) {
			return false;
		} else {
			return true;
		}
	}

	public String getDescricao() {
		return descricao;
	}

}
