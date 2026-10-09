package br.pucminas.sih.repository;

import br.pucminas.sih.model.Quarto;
import br.pucminas.sih.model.SituacaoQuarto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

	List<Quarto> findBySituacao(SituacaoQuarto situacao);

	boolean existsByNumero(String numero);

}
