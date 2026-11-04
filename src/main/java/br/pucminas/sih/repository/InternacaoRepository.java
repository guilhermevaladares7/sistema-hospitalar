package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.Internacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

	List<Internacao> findByDataEfetivaAltaIsNull();

	default Internacao buscarPorId(Long id) {
		Optional<Internacao> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("A internação " + id + " não foi encontrada.");
		}
		return encontrado.get();
	}

}
