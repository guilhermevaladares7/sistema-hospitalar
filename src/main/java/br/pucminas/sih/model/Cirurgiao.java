package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CIRURGIAO")
public class Cirurgiao extends ProfissionalSaude {

	@Enumerated(EnumType.STRING)
	@Column(name = "area_cirurgica")
	private AreaCirurgica areaCirurgica;

	@OneToMany(mappedBy = "profissional")
	private List<ProcedimentoCirurgico> procedimentos = new ArrayList<>();

	protected Cirurgiao() {
	}

	public Cirurgiao(String nome, String registroProfissional, AreaCirurgica areaCirurgica, String telefone, String email) {
		super(nome, registroProfissional, telefone, email);
		validarAreaCirurgica(areaCirurgica);
		this.areaCirurgica = areaCirurgica;
	}

	@Override
	public CategoriaProfissional getCategoria() {
		return CategoriaProfissional.CIRURGIAO;
	}

	@Override
	public boolean realizaProcedimentoCirurgico() {
		return true;
	}

	@Override
	public boolean possuiOutroProcedimentoNoHorario(ProcedimentoCirurgico procedimento) {
		for (ProcedimentoCirurgico outro : procedimentos) {
			if (outro != procedimento && outro.conflitaCom(procedimento.getDataHoraPrevista())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void adicionarProcedimento(ProcedimentoCirurgico procedimento) {
		if (procedimento.getProfissional() != this) {
			throw new RegraNegocioException("Esse procedimento é de outro cirurgião.");
		}
		if (procedimentos.contains(procedimento)) {
			throw new RegraNegocioException("Esse procedimento já está na agenda do cirurgião.");
		}
		procedimento.validarRealizacao();
		procedimentos.add(procedimento);
	}

	public List<ProcedimentoCirurgico> getProcedimentosRealizados() {
		List<ProcedimentoCirurgico> realizados = new ArrayList<>();
		for (ProcedimentoCirurgico procedimento : procedimentos) {
			if (procedimento.foiRealizado()) {
				realizados.add(procedimento);
			}
		}
		return realizados;
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

	public List<ProcedimentoCirurgico> getProcedimentos() {
		return procedimentos;
	}

}
