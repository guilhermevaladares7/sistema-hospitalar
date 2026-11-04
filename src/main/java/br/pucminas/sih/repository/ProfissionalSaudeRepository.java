package br.pucminas.sih.repository;

import br.pucminas.sih.model.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

	boolean existsByRegistroProfissional(String registroProfissional);

}
