package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.AtendimentoEmergencia;
import br.pucminas.sih.model.SituacaoEmergencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoEmergenciaRepository extends JpaRepository<AtendimentoEmergencia, Long> {

	List<AtendimentoEmergencia> findBySituacao(SituacaoEmergencia situacao);

	default AtendimentoEmergencia buscarPorId(Long id) {
		Optional<AtendimentoEmergencia> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("O atendimento de emergência " + id + " não foi encontrado.");
		}
		return encontrado.get();
	}

}
