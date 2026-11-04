package br.pucminas.sih.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HistoricoMedico {

	private final Paciente paciente;

	public HistoricoMedico(Paciente paciente) {
		this.paciente = paciente;
	}

	public List<Atendimento> getAtendimentos() {
		List<Atendimento> atendimentos = new ArrayList<>();
		atendimentos.addAll(paciente.getConsultas());
		atendimentos.addAll(paciente.getInternacoes());
		atendimentos.sort(Comparator.comparing(Atendimento::getDataReferencia).reversed());
		return atendimentos;
	}

	public List<Consulta> getConsultasConcluidas() {
		List<Consulta> concluidas = new ArrayList<>();
		for (Consulta consulta : paciente.getConsultas()) {
			if (consulta.getSituacao() == SituacaoConsulta.CONCLUIDA) {
				concluidas.add(consulta);
			}
		}
		return concluidas;
	}

	public List<Internacao> getInternacoes() {
		return new ArrayList<>(paciente.getInternacoes());
	}

	public Paciente getPaciente() {
		return paciente;
	}

}
