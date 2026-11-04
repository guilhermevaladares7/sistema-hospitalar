package br.pucminas.sih.model;

import br.pucminas.sih.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Encaminhamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "paciente_id")
	private Paciente paciente;

	@ManyToOne(optional = false)
	@JoinColumn(name = "clinico_id")
	private ProfissionalSaude clinico;

	@ManyToOne(optional = false)
	@JoinColumn(name = "consulta_origem_id")
	private ConsultaRotina consultaOrigem;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Especialidade especialidadeDestino;

	@Column(nullable = false, length = 2000)
	private String motivo;

	@Column(nullable = false)
	private LocalDate data;

	protected Encaminhamento() {
	}

	Encaminhamento(ConsultaRotina consultaOrigem, ProfissionalSaude clinico, Especialidade especialidadeDestino, String motivo, LocalDate data) {
		if (especialidadeDestino == null) {
			throw new RegraNegocioException("O encaminhamento precisa ter a especialidade de destino.");
		}
		if (motivo == null || motivo.isBlank()) {
			throw new RegraNegocioException("O encaminhamento precisa ter um motivo.");
		}
		if (data == null) {
			throw new RegraNegocioException("O encaminhamento precisa ter uma data.");
		}
		this.consultaOrigem = consultaOrigem;
		this.paciente = consultaOrigem.getPaciente();
		this.clinico = clinico;
		this.especialidadeDestino = especialidadeDestino;
		this.motivo = motivo;
		this.data = data;
	}

	public Long getId() {
		return id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public ProfissionalSaude getClinico() {
		return clinico;
	}

	public ConsultaRotina getConsultaOrigem() {
		return consultaOrigem;
	}

	public Especialidade getEspecialidadeDestino() {
		return especialidadeDestino;
	}

	public String getMotivo() {
		return motivo;
	}

	public LocalDate getData() {
		return data;
	}

}
