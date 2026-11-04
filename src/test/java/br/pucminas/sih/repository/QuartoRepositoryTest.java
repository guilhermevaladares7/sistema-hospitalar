package br.pucminas.sih.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucminas.sih.model.Apartamento;
import br.pucminas.sih.model.Enfermaria;
import br.pucminas.sih.model.Quarto;
import br.pucminas.sih.model.SituacaoQuarto;
import br.pucminas.sih.model.TipoLeito;
import br.pucminas.sih.model.Uti;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuartoRepositoryTest {

	@Autowired
	private QuartoRepository quartoRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void salvaERecuperaCadaTipoDeQuarto() {
		Enfermaria enfermaria = new Enfermaria("101", 1, new BigDecimal("300.00"), false, false, 4);
		Apartamento apartamento = new Apartamento("301", 3, new BigDecimal("500.00"), true, true, TipoLeito.ELETRICO, true);
		Uti uti = new Uti("401", 4, new BigDecimal("999.99"), true, false, 2, true);
		quartoRepository.saveAll(List.of(enfermaria, apartamento, uti));
		entityManager.flush();
		entityManager.clear();

		Quarto enfermariaSalva = quartoRepository.findById(enfermaria.getId()).orElseThrow();
		Quarto apartamentoSalvo = quartoRepository.findById(apartamento.getId()).orElseThrow();
		Quarto utiSalva = quartoRepository.findById(uti.getId()).orElseThrow();

		assertEquals(4, assertInstanceOf(Enfermaria.class, enfermariaSalva).getQuantidadeLeitos());
		assertEquals(new BigDecimal("255.00"), enfermariaSalva.calcularDiaria(false));
		assertEquals(TipoLeito.ELETRICO, assertInstanceOf(Apartamento.class, apartamentoSalvo).getTipoLeito());
		assertEquals(new BigDecimal("670.00"), apartamentoSalvo.calcularDiaria(true));
		assertTrue(assertInstanceOf(Uti.class, utiSalva).isPossuiIsolamento());
		assertEquals(new BigDecimal("999.99"), utiSalva.getValorBaseDiaria());
		assertEquals(2, utiSalva.getCapacidadeMaxima());
		assertEquals(3, quartoRepository.findBySituacao(SituacaoQuarto.DISPONIVEL).size());
	}

}
