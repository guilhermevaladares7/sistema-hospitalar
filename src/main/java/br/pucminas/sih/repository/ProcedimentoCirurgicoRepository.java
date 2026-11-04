package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.ProcedimentoCirurgico;
import br.pucminas.sih.model.SituacaoProcedimento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedimentoCirurgicoRepository extends JpaRepository<ProcedimentoCirurgico, Long> {

	List<ProcedimentoCirurgico> findBySituacao(SituacaoProcedimento situacao);

	default ProcedimentoCirurgico buscarPorId(Long id) {
		Optional<ProcedimentoCirurgico> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("O procedimento cirúrgico " + id + " não foi encontrado.");
		}
		return encontrado.get();
	}

}
