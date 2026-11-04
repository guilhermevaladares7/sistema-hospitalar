package br.pucminas.sih.repository;

import br.pucminas.sih.model.ProcedimentoCirurgico;
import br.pucminas.sih.model.SituacaoProcedimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedimentoCirurgicoRepository extends JpaRepository<ProcedimentoCirurgico, Long> {

	List<ProcedimentoCirurgico> findBySituacao(SituacaoProcedimento situacao);

}
