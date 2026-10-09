package br.pucminas.sih.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

class Fixtures {

	static final LocalDate QUARTA = LocalDate.of(2026, 10, 14);
	static final LocalDate QUINTA = LocalDate.of(2026, 10, 15);

	static Paciente paciente(String nome, String cpf) {
		Endereco endereco = new Endereco("Rua dos Timbiras", "1200", "apto 302", "Funcionários", "Belo Horizonte", "MG", "30140-060");
		return new Paciente(nome, cpf, LocalDate.of(1991, 3, 14), "(31) 98812-3401", "paciente@email.com", endereco);
	}

	static ProfissionalSaude cardiologistaDeQuarta() {
		ProfissionalSaude profissional = new ProfissionalSaude("Dra. Helena Duarte", "CRM-MG 45.812", Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena.duarte@sih.com.br");
		profissional.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(14, 0)));
		return profissional;
	}

}
