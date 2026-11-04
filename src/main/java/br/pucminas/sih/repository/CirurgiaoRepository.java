package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.Cirurgiao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CirurgiaoRepository extends JpaRepository<Cirurgiao, Long> {

	default Cirurgiao buscarPorId(Long id) {
		Optional<Cirurgiao> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("O cirurgião " + id + " não foi encontrado.");
		}
		return encontrado.get();
	}

}
