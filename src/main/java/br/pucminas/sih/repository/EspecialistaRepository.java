package br.pucminas.sih.repository;

import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Especialista;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialistaRepository extends JpaRepository<Especialista, Long> {

	List<Especialista> findByEspecialidade(Especialidade especialidade);

}
