package br.pucminas.sih.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucminas.sih.exception.RegraNegocioException;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncaminhamentoTest {

	private Paciente ana;
	private ClinicoGeral rafael;
	private ConsultaRotina consulta;

	@BeforeEach
	void agendar() {
		ana = Fixtures.paciente("Ana Beatriz Moreira", "123.456.789-00");
		rafael = Fixtures.clinicoDeQuarta();
		consulta = Fixtures.rotina(ana, rafael, Fixtures.QUARTA, LocalTime.of(9, 0));
		rafael.adicionarConsulta(consulta);
	}

	private void concluirConsulta() {
		consulta.confirmar();
		consulta.iniciar();
		consulta.concluir("Sopro cardíaco a investigar");
	}

	@Test
	void clinicoEncaminhaAPartirDeConsultaConcluida() {
		concluirConsulta();

		Encaminhamento encaminhamento = rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro cardíaco", Fixtures.QUARTA);

		assertSame(ana, encaminhamento.getPaciente());
		assertSame(rafael, encaminhamento.getClinico());
		assertSame(consulta, encaminhamento.getConsultaOrigem());
		assertEquals(Especialidade.CARDIOLOGIA, encaminhamento.getEspecialidadeDestino());
		assertEquals("Sopro cardíaco", encaminhamento.getMotivo());
		assertEquals(Fixtures.QUARTA, encaminhamento.getData());
	}

	@Test
	void naoEncaminhaDeConsultaQueNaoFoiConcluida() {
		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro", Fixtures.QUARTA));

		consulta.confirmar();
		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro", Fixtures.QUARTA));

		consulta.iniciar();
		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro", Fixtures.QUARTA));
	}

	@Test
	void naoEncaminhaDeConsultaCancelada() {
		consulta.cancelar();

		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro", Fixtures.QUARTA));
	}

	@Test
	void especialistaECirurgiaoNaoEncaminham() {
		concluirConsulta();
		Especialista helena = Fixtures.cardiologistaDeQuarta();
		Cirurgiao marcos = Fixtures.cirurgiaoDeQuarta();

		assertThrows(RegraNegocioException.class, () -> helena.encaminhar(consulta, Especialidade.NEUROLOGIA, "Cefaleia", Fixtures.QUARTA));
		assertThrows(RegraNegocioException.class, () -> marcos.encaminhar(consulta, Especialidade.ORTOPEDIA, "Joelho", Fixtures.QUARTA));
	}

	@Test
	void clinicoSoEncaminhaDasSuasConsultas() {
		concluirConsulta();
		ClinicoGeral outro = Fixtures.clinicoDeQuarta();

		assertThrows(RegraNegocioException.class, () -> outro.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro", Fixtures.QUARTA));
	}

	@Test
	void exigeEspecialidadeMotivoEData() {
		concluirConsulta();

		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, null, "Sopro", Fixtures.QUARTA));
		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, " ", Fixtures.QUARTA));
		assertThrows(RegraNegocioException.class, () -> rafael.encaminhar(consulta, Especialidade.CARDIOLOGIA, "Sopro", null));
	}

}
