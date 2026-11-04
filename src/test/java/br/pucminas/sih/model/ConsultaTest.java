package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsultaTest {

	private ClinicoGeral rafael;
	private ConsultaRotina consulta;

	@BeforeEach
	void agendar() {
		rafael = Fixtures.clinicoDeQuarta();
		Paciente ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		consulta = Fixtures.rotina(ana, rafael, Fixtures.QUARTA, LocalTime.of(9, 0));
		rafael.adicionarConsulta(consulta);
	}

	@Test
	void comecaAgendada() {
		assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
	}

	@Test
	void fluxoCompletoAteConcluir() {
		consulta.confirmar();
		assertEquals(SituacaoConsulta.CONFIRMADA, consulta.getSituacao());

		consulta.iniciar();
		assertEquals(SituacaoConsulta.EM_ATENDIMENTO, consulta.getSituacao());

		consulta.concluir("Pressão normal, retorno em 6 meses");
		assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
		assertEquals("Pressão normal, retorno em 6 meses", consulta.getObservacoes());
	}

	@Test
	void cancelaAgendadaOuConfirmada() {
		consulta.cancelar();
		assertEquals(SituacaoConsulta.CANCELADA, consulta.getSituacao());

		ConsultaRotina outra = Fixtures.rotina(consulta.getPaciente(), rafael, Fixtures.QUARTA, LocalTime.of(10, 0));
		outra.confirmar();
		outra.cancelar();
		assertEquals(SituacaoConsulta.CANCELADA, outra.getSituacao());
	}

	@Test
	void registraNaoComparecimentoDeAgendadaOuConfirmada() {
		consulta.registrarNaoComparecimento();
		assertEquals(SituacaoConsulta.NAO_COMPARECEU, consulta.getSituacao());

		ConsultaRotina outra = Fixtures.rotina(consulta.getPaciente(), rafael, Fixtures.QUARTA, LocalTime.of(10, 0));
		outra.confirmar();
		outra.registrarNaoComparecimento();
		assertEquals(SituacaoConsulta.NAO_COMPARECEU, outra.getSituacao());
	}

	@Test
	void naoIniciaNemConcluiSemConfirmar() {
		assertThrows(RegraNegocioException.class, consulta::iniciar);
		assertThrows(RegraNegocioException.class, () -> consulta.concluir("Tudo certo"));
		assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
		assertNull(consulta.getObservacoes());
	}

	@Test
	void emAtendimentoNaoPodeSerCanceladaNemVirarFalta() {
		consulta.confirmar();
		consulta.iniciar();

		assertThrows(RegraNegocioException.class, consulta::cancelar);
		assertThrows(RegraNegocioException.class, consulta::registrarNaoComparecimento);
		assertThrows(RegraNegocioException.class, consulta::confirmar);
		assertEquals(SituacaoConsulta.EM_ATENDIMENTO, consulta.getSituacao());
	}

	@Test
	void concluidaNaoMudaMais() {
		consulta.confirmar();
		consulta.iniciar();
		consulta.concluir("Tudo certo");

		assertThrows(RegraNegocioException.class, consulta::confirmar);
		assertThrows(RegraNegocioException.class, consulta::cancelar);
		assertThrows(RegraNegocioException.class, () -> consulta.concluir("De novo"));
		assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
		assertEquals("Tudo certo", consulta.getObservacoes());
	}

	@Test
	void canceladaNaoPodeSerConcluidaNemRetomada() {
		consulta.cancelar();

		assertThrows(RegraNegocioException.class, consulta::confirmar);
		assertThrows(RegraNegocioException.class, consulta::iniciar);
		assertThrows(RegraNegocioException.class, () -> consulta.concluir("Tudo certo"));
		assertEquals(SituacaoConsulta.CANCELADA, consulta.getSituacao());
	}

	@Test
	void reagendarCriaUmaConsultaNovaNoMesmoHorario() {
		consulta.cancelar();

		ConsultaRotina nova = Fixtures.rotina(consulta.getPaciente(), rafael, Fixtures.QUARTA, LocalTime.of(9, 0));
		rafael.adicionarConsulta(nova);

		assertEquals(SituacaoConsulta.CANCELADA, consulta.getSituacao());
		assertEquals(SituacaoConsulta.AGENDADA, nova.getSituacao());
		assertEquals(2, rafael.getConsultas().size());
	}

	@Test
	void exigeDataHorarioEMotivo() {
		Paciente ana = consulta.getPaciente();
		assertThrows(RegraNegocioException.class, () -> new ConsultaRotina(ana, rafael, null, LocalTime.of(9, 0), "Check-up"));
		assertThrows(RegraNegocioException.class, () -> new ConsultaRotina(ana, rafael, Fixtures.QUARTA, null, "Check-up"));
		assertThrows(RegraNegocioException.class, () -> new ConsultaRotina(ana, rafael, Fixtures.QUARTA, LocalTime.of(9, 0), " "));
	}

}
