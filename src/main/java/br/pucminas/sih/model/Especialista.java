package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("ESPECIALISTA")
public class Especialista extends ProfissionalSaude {

	@Enumerated(EnumType.STRING)
	@Column(name = "especialidade")
	private Especialidade especialidade;

	protected Especialista() {
	}

	public Especialista(String nome, String registroProfissional, Especialidade especialidade, String telefone, String email) {
		super(nome, registroProfissional, telefone, email);
		validarEspecialidade(especialidade);
		this.especialidade = especialidade;
	}

	@Override
	public boolean atuaEm(Especialidade especialidade) {
		if (this.especialidade == especialidade) {
			return true;
		} else {
			return false;
		}
	}

	private void validarEspecialidade(Especialidade especialidade) {
		if (especialidade == null) {
			throw new RegraNegocioException("O especialista precisa ter uma especialidade.");
		}
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		validarEspecialidade(especialidade);
		this.especialidade = especialidade;
	}

}
