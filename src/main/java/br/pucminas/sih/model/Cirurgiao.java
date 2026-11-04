package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("CIRURGIAO")
public class Cirurgiao extends ProfissionalSaude {

	@Enumerated(EnumType.STRING)
	@Column(name = "area_cirurgica")
	private AreaCirurgica areaCirurgica;

	protected Cirurgiao() {
	}

	public Cirurgiao(String nome, String registroProfissional, AreaCirurgica areaCirurgica, String telefone, String email) {
		super(nome, registroProfissional, telefone, email);
		validarAreaCirurgica(areaCirurgica);
		this.areaCirurgica = areaCirurgica;
	}

	private void validarAreaCirurgica(AreaCirurgica areaCirurgica) {
		if (areaCirurgica == null) {
			throw new RegraNegocioException("O cirurgião precisa ter uma área cirúrgica.");
		}
	}

	public AreaCirurgica getAreaCirurgica() {
		return areaCirurgica;
	}

	public void setAreaCirurgica(AreaCirurgica areaCirurgica) {
		validarAreaCirurgica(areaCirurgica);
		this.areaCirurgica = areaCirurgica;
	}

}
