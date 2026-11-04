package br.pucminas.sih.repository;

import br.pucminas.sih.exception.RecursoNaoEncontradoException;
import br.pucminas.sih.model.Consulta;
import br.pucminas.sih.model.ProfissionalSaude;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	List<Consulta> findByData(LocalDate data);

	List<Consulta> findByProfissionalAndData(ProfissionalSaude profissional, LocalDate data);

	default Consulta buscarPorId(Long id) {
		Optional<Consulta> encontrado = findById(id);
		if (encontrado.isEmpty()) {
			throw new RecursoNaoEncontradoException("A consulta " + id + " não foi encontrada.");
		}
		return encontrado.get();
	}

}
