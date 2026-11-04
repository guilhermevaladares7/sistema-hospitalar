package br.pucminas.sih.dto;

import br.pucminas.sih.model.AreaCirurgica;
import br.pucminas.sih.model.CategoriaProfissional;
import br.pucminas.sih.model.Cirurgiao;
import br.pucminas.sih.model.Disponibilidade;
import br.pucminas.sih.model.Especialidade;
import br.pucminas.sih.model.Especialista;
import br.pucminas.sih.model.ProfissionalSaude;
import java.util.ArrayList;
import java.util.List;

public record ProfissionalResponse(
		Long id,
		CategoriaProfissional categoria,
		String nome,
		String registroProfissional,
		Especialidade especialidade,
		AreaCirurgica areaCirurgica,
		String telefone,
		String email,
		List<DisponibilidadeResponse> disponibilidades) {

	public static ProfissionalResponse de(ProfissionalSaude profissional) {
		Especialidade especialidade = null;
		AreaCirurgica areaCirurgica = null;
		if (profissional instanceof Especialista especialista) {
			especialidade = especialista.getEspecialidade();
		}
		if (profissional instanceof Cirurgiao cirurgiao) {
			areaCirurgica = cirurgiao.getAreaCirurgica();
		}
		List<DisponibilidadeResponse> disponibilidades = new ArrayList<>();
		for (Disponibilidade disponibilidade : profissional.getDisponibilidades()) {
			disponibilidades.add(DisponibilidadeResponse.de(disponibilidade));
		}
		return new ProfissionalResponse(profissional.getId(), profissional.getCategoria(), profissional.getNome(), profissional.getRegistroProfissional(), especialidade, areaCirurgica, profissional.getTelefone(), profissional.getEmail(), disponibilidades);
	}

}
