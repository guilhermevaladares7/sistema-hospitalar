package br.pucminas.sih.repository;

import br.pucminas.sih.model.Paciente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

	Optional<Paciente> findByCpf(String cpf);

	boolean existsByCpf(String cpf);

}
