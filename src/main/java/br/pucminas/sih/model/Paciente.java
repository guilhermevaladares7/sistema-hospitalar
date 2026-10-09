package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Paciente extends Pessoa {

	@Column(nullable = false, unique = true)
	private String cpf;

	@Column(nullable = false)
	private LocalDate dataNascimento;

	@Embedded
	private Endereco endereco;

	@OneToMany(mappedBy = "paciente")
	private List<Consulta> consultas = new ArrayList<>();

	@OneToMany(mappedBy = "paciente")
	private List<Internacao> internacoes = new ArrayList<>();

	protected Paciente() {
	}

	public Paciente(String nome, String cpf, LocalDate dataNascimento, String telefone, String email, Endereco endereco) {
		super(nome, telefone, email);
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.endereco = endereco;
	}

	public int getIdade() {
		return Period.between(dataNascimento, LocalDate.now()).getYears();
	}

	public void adicionarConsulta(Consulta consulta) {
		consultas.add(consulta);
	}

	public void adicionarInternacao(Internacao internacao) {
		if (possuiInternacaoAtiva()) {
			throw new RegraNegocioException("O paciente já possui uma internação ativa.");
		}
		internacoes.add(internacao);
	}

	public boolean possuiInternacaoAtiva() {
		for (Internacao internacao : internacoes) {
			if (internacao.estaAtiva()) {
				return true;
			}
		}
		return false;
	}

	public HistoricoMedico getHistorico() {
		return new HistoricoMedico(this);
	}

	public String getCpf() {
		return cpf;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Consulta> getConsultas() {
		return consultas;
	}

	public List<Internacao> getInternacoes() {
		return internacoes;
	}

}
