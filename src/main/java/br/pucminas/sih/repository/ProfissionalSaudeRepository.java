package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.ProfissionalSaude;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

	boolean existsByRegistroProfissional(String registroProfissional);

	default ProfissionalSaude buscarPorId(Long id) {
		Optional<ProfissionalSaude> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("O profissional " + id + " não foi encontrado.");
		}
		return encontrado.get();
	}

}
