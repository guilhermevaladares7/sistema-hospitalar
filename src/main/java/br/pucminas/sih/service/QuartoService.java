package br.pucminas.sih.service;

import br.pucminas.sih.dto.ApartamentoRequest;
import br.pucminas.sih.dto.EnfermariaRequest;
import br.pucminas.sih.dto.QuartoResponse;
import br.pucminas.sih.dto.UtiRequest;
import br.pucminas.sih.exception.RegraNegocioException;
import br.pucminas.sih.model.Apartamento;
import br.pucminas.sih.model.Enfermaria;
import br.pucminas.sih.model.Quarto;
import br.pucminas.sih.model.SituacaoQuarto;
import br.pucminas.sih.model.Uti;
import br.pucminas.sih.repository.QuartoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuartoService {

	private final QuartoRepository quartoRepository;

	public QuartoService(QuartoRepository quartoRepository) {
		this.quartoRepository = quartoRepository;
	}

	@Transactional
	public QuartoResponse cadastrarEnfermaria(EnfermariaRequest request) {
		validarNumeroUnico(request.numero());
		Enfermaria enfermaria = new Enfermaria(request.numero(), request.andar(), request.valorBaseDiaria(), request.possuiArCondicionado(), request.possuiBanheiroPrivativo(), request.quantidadeLeitos());
		return salvar(enfermaria);
	}

	@Transactional
	public QuartoResponse cadastrarApartamento(ApartamentoRequest request) {
		validarNumeroUnico(request.numero());
		Apartamento apartamento = new Apartamento(request.numero(), request.andar(), request.valorBaseDiaria(), request.possuiArCondicionado(), request.possuiBanheiroPrivativo(), request.tipoLeito(), request.possuiLeitoAcompanhante());
		return salvar(apartamento);
	}

	@Transactional
	public QuartoResponse cadastrarUti(UtiRequest request) {
		validarNumeroUnico(request.numero());
		Uti uti = new Uti(request.numero(), request.andar(), request.valorBaseDiaria(), request.possuiArCondicionado(), request.possuiBanheiroPrivativo(), request.quantidadeLeitos(), request.possuiIsolamento());
		return salvar(uti);
	}

	public QuartoResponse buscar(Long id) {
		return QuartoResponse.de(quartoRepository.buscarPorId(id));
	}

	public List<QuartoResponse> listar() {
		return converter(quartoRepository.findAll());
	}

	public List<QuartoResponse> listarDisponiveis() {
		return converter(quartoRepository.findBySituacao(SituacaoQuarto.DISPONIVEL));
	}

	private void validarNumeroUnico(String numero) {
		if (quartoRepository.existsByNumero(numero)) {
			throw new RegraNegocioException("Já existe um quarto cadastrado com o número " + numero + ".");
		}
	}

	private QuartoResponse salvar(Quarto quarto) {
		quartoRepository.save(quarto);
		return QuartoResponse.de(quarto);
	}

	private List<QuartoResponse> converter(List<Quarto> quartos) {
		List<QuartoResponse> respostas = new ArrayList<>();
		for (Quarto quarto : quartos) {
			respostas.add(QuartoResponse.de(quarto));
		}
		return respostas;
	}

}
