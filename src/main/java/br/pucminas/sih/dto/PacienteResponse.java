package br.pucminas.sih.dto;

import br.pucminas.sih.model.Paciente;
import java.time.LocalDate;

public record PacienteResponse(
		Long id,
		String nome,
		String cpf,
		LocalDate dataNascimento,
		int idade,
		String telefone,
		String email,
		EnderecoResponse endereco,
		boolean possuiInternacaoAtiva) {

	public static PacienteResponse de(Paciente paciente) {
		return new PacienteResponse(paciente.getId(), paciente.getNome(), paciente.getCpf(), paciente.getDataNascimento(), paciente.getIdade(), paciente.getTelefone(), paciente.getEmail(), EnderecoResponse.de(paciente.getEndereco()), paciente.possuiInternacaoAtiva());
	}

}
