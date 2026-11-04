package br.pucminas.sih.repository;

import br.pucminas.sih.model.AtendimentoEmergencia;
import br.pucminas.sih.model.SituacaoEmergencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoEmergenciaRepository extends JpaRepository<AtendimentoEmergencia, Long> {

	List<AtendimentoEmergencia> findBySituacao(SituacaoEmergencia situacao);

}
