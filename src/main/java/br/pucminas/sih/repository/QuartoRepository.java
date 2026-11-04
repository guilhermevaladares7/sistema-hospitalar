package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.Quarto;
import br.pucminas.sih.model.SituacaoQuarto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

	List<Quarto> findBySituacao(SituacaoQuarto situacao);

	boolean existsByNumero(String numero);

	default Quarto buscarPorId(Long id) {
		Optional<Quarto> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("O quarto " + id + " não foi encontrado.");
		}
		return encontrado.get();
	}

}
