package br.pucminas.sih.repository;

import br.pucminas.sih.model.Encaminhamento;
import br.pucminas.sih.model.Paciente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncaminhamentoRepository extends JpaRepository<Encaminhamento, Long> {

	List<Encaminhamento> findByPaciente(Paciente paciente);

}
