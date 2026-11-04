package br.pucminas.sih.dto;

import br.pucminas.sih.model.Apartamento;
import br.pucminas.sih.model.Quarto;
import br.pucminas.sih.model.SituacaoQuarto;
import br.pucminas.sih.model.TipoLeito;
import br.pucminas.sih.model.TipoQuarto;
import br.pucminas.sih.model.Uti;
import java.math.BigDecimal;

public record QuartoResponse(
		Long id,
		TipoQuarto tipo,
		String numero,
		int andar,
		BigDecimal valorBaseDiaria,
		boolean possuiArCondicionado,
		boolean possuiBanheiroPrivativo,
		SituacaoQuarto situacao,
		int capacidadeMaxima,
		int ocupacaoAtual,
		boolean permiteAcompanhante,
		TipoLeito tipoLeito,
		Boolean possuiIsolamento,
		BigDecimal valorDiaria,
		BigDecimal valorDiariaComAcompanhante) {

	public static QuartoResponse de(Quarto quarto) {
		TipoLeito tipoLeito = null;
		Boolean possuiIsolamento = null;
		if (quarto instanceof Apartamento apartamento) {
			tipoLeito = apartamento.getTipoLeito();
		}
		if (quarto instanceof Uti uti) {
			possuiIsolamento = uti.isPossuiIsolamento();
		}
		BigDecimal valorDiariaComAcompanhante = null;
		if (quarto.permiteAcompanhante()) {
			valorDiariaComAcompanhante = quarto.calcularDiaria(true);
		}
		return new QuartoResponse(quarto.getId(), quarto.getTipo(), quarto.getNumero(), quarto.getAndar(), quarto.getValorBaseDiaria(), quarto.isPossuiArCondicionado(), quarto.isPossuiBanheiroPrivativo(), quarto.getSituacao(), quarto.getCapacidadeMaxima(), quarto.getOcupacaoAtual(), quarto.permiteAcompanhante(), tipoLeito, possuiIsolamento, quarto.calcularDiaria(false), valorDiariaComAcompanhante);
	}

}
