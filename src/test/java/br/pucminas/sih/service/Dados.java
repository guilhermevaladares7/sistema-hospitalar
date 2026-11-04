package br.pucminas.sih.service;

import br.pucminas.sih.dto.ApartamentoRequest;
import br.pucminas.sih.dto.CirurgiaoRequest;
import br.pucminas.sih.dto.ClinicoGeralRequest;
import br.pucminas.sih.dto.DisponibilidadeRequest;
import br.pucminas.sih.dto.EnderecoRequest;
import br.pucminas.sih.dto.EnfermariaRequest;
import br.pucminas.sih.dto.EspecialistaRequest;
import br.pucminas.sih.dto.PacienteRequest;
import br.pucminas.sih.dto.UtiRequest;
import br.pucminas.sih.model.AreaCirurgica;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.TipoLeito;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

class Dados {

	static final LocalDate PROXIMA_QUARTA = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

	static PacienteRequest paciente(String nome, String cpf) {
		EnderecoRequest endereco = new EnderecoRequest("Rua dos Timbiras", "1200", "apto 302", "Funcionários", "Belo Horizonte", "MG", "30140-060");
		return new PacienteRequest(nome, cpf, LocalDate.of(1991, 3, 14), "(31) 98812-3401", "paciente@email.com", endereco);
	}

	static List<DisponibilidadeRequest> quartaDas(int inicio, int fim) {
		return List.of(new DisponibilidadeRequest(DayOfWeek.WEDNESDAY, LocalTime.of(inicio, 0), LocalTime.of(fim, 0)));
	}

	static ClinicoGeralRequest clinico(String registro) {
		return new ClinicoGeralRequest("Dr. Rafael Teixeira", registro, "(31) 3290-1002", "rafael.teixeira@sih.com.br", quartaDas(8, 12));
	}

	static EspecialistaRequest cardiologista(String registro) {
		return new EspecialistaRequest("Dra. Helena Duarte", registro, Especialidade.CARDIOLOGIA, "(31) 3290-1001", "helena.duarte@sih.com.br", quartaDas(8, 14));
	}

	static CirurgiaoRequest cirurgiao(String registro) {
		return new CirurgiaoRequest("Dr. Marcos Vinícius Prado", registro, AreaCirurgica.ORTOPEDICA, "(31) 3290-1003", "marcos.prado@sih.com.br", quartaDas(13, 18));
	}

	static EnfermariaRequest enfermaria(String numero, int leitos) {
		return new EnfermariaRequest(numero, 1, new BigDecimal("300.00"), false, false, leitos);
	}

	static ApartamentoRequest apartamento(String numero, TipoLeito tipoLeito, boolean possuiLeitoAcompanhante) {
		return new ApartamentoRequest(numero, 3, new BigDecimal("500.00"), true, true, tipoLeito, possuiLeitoAcompanhante);
	}

	static UtiRequest uti(String numero, int leitos, boolean possuiIsolamento) {
		return new UtiRequest(numero, 4, new BigDecimal("1000.00"), true, false, leitos, possuiIsolamento);
	}

}
