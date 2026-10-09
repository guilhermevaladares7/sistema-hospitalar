package br.pucminas.sih.repository;

import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.ProfissionalSaude;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

	List<ProfissionalSaude> findByEspecialidade(Especialidade especialidade);

	boolean existsByRegistroProfissional(String registroProfissional);

}
