package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ProfissionalSaude extends Pessoa {

	@Column(nullable = false, unique = true)
	private String registroProfissional;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Especialidade especialidade;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "profissional_id")
	private List<Disponibilidade> disponibilidades = new ArrayList<>();

	@OneToMany(mappedBy = "profissional")
	private List<Consulta> consultas = new ArrayList<>();

	@OneToMany(mappedBy = "profissional")
	private List<Internacao> internacoes = new ArrayList<>();

	protected ProfissionalSaude() {
	}

	public ProfissionalSaude(String nome, String registroProfissional, Especialidade especialidade, String telefone, String email) {
		super(nome, telefone, email);
		this.registroProfissional = registroProfissional;
		this.especialidade = especialidade;
	}

	public void adicionarDisponibilidade(Disponibilidade disponibilidade) {
		disponibilidades.add(disponibilidade);
	}

	public void adicionarConsulta(Consulta consulta) {
		if (!estaDisponivel(consulta.getData(), consulta.getHorario())) {
			throw new RegraNegocioException("O profissional não está disponível nesse dia e horário.");
		}
		consultas.add(consulta);
	}

	public void adicionarInternacao(Internacao internacao) {
		internacoes.add(internacao);
	}

	public boolean atendeNoHorario(LocalDate data, LocalTime horario) {
		for (Disponibilidade disponibilidade : disponibilidades) {
			if (disponibilidade.contem(data, horario)) {
				return true;
			}
		}
		return false;
	}

	public boolean possuiConsultaNoHorario(LocalDate data, LocalTime horario) {
		for (Consulta consulta : consultas) {
			if (consulta.conflitaCom(data, horario)) {
				return true;
			}
		}
		return false;
	}

	public boolean estaDisponivel(LocalDate data, LocalTime horario) {
		if (!atendeNoHorario(data, horario)) {
			return false;
		}
		if (possuiConsultaNoHorario(data, horario)) {
			return false;
		} else {
			return true;
		}
	}

	public String getRegistroProfissional() {
		return registroProfissional;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public List<Disponibilidade> getDisponibilidades() {
		return disponibilidades;
	}

	public List<Consulta> getConsultas() {
		return consultas;
	}

	public List<Internacao> getInternacoes() {
		return internacoes;
	}

}
