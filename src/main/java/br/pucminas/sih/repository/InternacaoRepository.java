package br.pucminas.sih.repository;

import br.pucminas.sih.model.Internacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

	List<Internacao> findByDataEfetivaAltaIsNull();

}
