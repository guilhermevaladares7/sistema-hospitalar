package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;

public enum SituacaoEmergencia {

	AGUARDANDO_AVALIACAO("aguardando avaliação") {
		@Override
		public boolean podeMudarPara(SituacaoEmergencia nova) {
			if (nova == EM_ATENDIMENTO) {
				return true;
			} else {
				return false;
			}
		}
	},

	EM_ATENDIMENTO("em atendimento") {
		@Override
		public boolean podeMudarPara(SituacaoEmergencia nova) {
			if (nova == CONCLUIDO) {
				return true;
			} else {
				return false;
			}
		}
	},

	CONCLUIDO("concluído") {
		@Override
		public boolean podeMudarPara(SituacaoEmergencia nova) {
			return false;
		}
	};

	private final String descricao;

	SituacaoEmergencia(String descricao) {
		this.descricao = descricao;
	}

	public abstract boolean podeMudarPara(SituacaoEmergencia nova);

	public void validarMudancaPara(SituacaoEmergencia nova) {
		if (!podeMudarPara(nova)) {
			throw new RegraNegocioException("Um atendimento de emergência " + descricao + " não pode passar para " + nova.getDescricao() + ".");
		}
	}

	public String getDescricao() {
		return descricao;
	}

}
