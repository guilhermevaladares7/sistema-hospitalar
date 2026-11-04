package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.ConsultaRotina;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRotinaRepository extends JpaRepository<ConsultaRotina, Long> {

	default ConsultaRotina buscarPorId(Long id) {
		Optional<ConsultaRotina> encontrada = findById(id);
		if (encontrada.isEmpty()) {
			throw new RecursoNaoEncontradoException("A consulta de rotina " + id + " não foi encontrada.");
		}
		return encontrada.get();
	}

}
