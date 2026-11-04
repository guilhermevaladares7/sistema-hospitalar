package br.pucminas.sih.model;

import java.math.BigDecimal;
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

	static Especialista cardiologistaDeQuarta() {
		Especialista profissional = new Especialista("Dra. Helena Duarte", "CRM-MG 45.812", Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena.duarte@sih.com.br");
		profissional.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(14, 0)));
		return profissional;
	}

	static ClinicoGeral clinicoDeQuarta() {
		ClinicoGeral profissional = new ClinicoGeral("Dr. Rafael Teixeira", "CRM-MG 38.207", "(31) 3290-1002", "rafael.teixeira@sih.com.br");
		profissional.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)));
		return profissional;
	}

	static Enfermaria enfermaria(String numero, int leitos) {
		return new Enfermaria(numero, 1, new BigDecimal("300.00"), false, false, leitos);
	}

	static Apartamento apartamento(String numero, TipoLeito tipoLeito, boolean possuiLeitoAcompanhante) {
		return new Apartamento(numero, 3, new BigDecimal("500.00"), true, true, tipoLeito, possuiLeitoAcompanhante);
	}

	static Uti uti(String numero, int leitos, boolean possuiIsolamento) {
		return new Uti(numero, 4, new BigDecimal("1000.00"), true, false, leitos, possuiIsolamento);
	}

	static ConsultaRotina rotina(Paciente paciente, ProfissionalSaude profissional, LocalDate data, LocalTime horario) {
		return new ConsultaRotina(paciente, profissional, data, horario, "Check-up");
	}

	static ConsultaEspecializada cardiologia(Paciente paciente, ProfissionalSaude profissional, LocalDate data, LocalTime horario) {
		return new ConsultaEspecializada(paciente, profissional, Especialidade.CARDIOLOGIA, data, horario, "Palpitações");
	}

	static Cirurgiao cirurgiaoDeQuarta() {
		Cirurgiao profissional = new Cirurgiao("Dr. Marcos Vinícius Prado", "CRM-MG 51.334", AreaCirurgica.ORTOPEDICA, "(31) 3290-1003", "marcos.prado@sih.com.br");
		profissional.adicionarDisponibilidade(new Disponibilidade(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(18, 0)));
		return profissional;
	}

}
