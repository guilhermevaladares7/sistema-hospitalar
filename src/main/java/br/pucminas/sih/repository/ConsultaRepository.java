package br.pucminas.sih.repository;

import br.pucminas.sih.model.Consulta;
import br.pucminas.sih.model.ProfissionalSaude;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	List<Consulta> findByData(LocalDate data);

	List<Consulta> findByProfissionalAndData(ProfissionalSaude profissional, LocalDate data);

}
