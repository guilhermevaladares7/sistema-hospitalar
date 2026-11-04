package br.pucminas.sih.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLINICO_GERAL")
public class ClinicoGeral extends ProfissionalSaude {

	protected ClinicoGeral() {
	}

	public ClinicoGeral(String nome, String registroProfissional, String telefone, String email) {
		super(nome, registroProfissional, telefone, email);
	}

	@Override
	public boolean podeEncaminhar() {
		return true;
	}

}
