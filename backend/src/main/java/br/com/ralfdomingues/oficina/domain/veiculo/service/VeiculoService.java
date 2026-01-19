package br.com.ralfdomingues.oficina.domain.veiculo.service;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.veiculo.dto.*;
import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import br.com.ralfdomingues.oficina.repository.veiculo.VeiculoRepository;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de serviço responsável pelas regras de negócio
 * relacionadas à entidade {@link Veiculo}.
 *
 * <p>Centraliza operações de cadastro, atualização,
 * listagem e exclusão lógica de veículos.</p>
 */
@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    /**
     * Cria um novo veículo associado a um cliente existente.
     *
     * @throws NotFoundException  caso o cliente não exista
     * @throws BusinessException caso a placa já esteja cadastrada
     */
    @Transactional
    public VeiculoResponseDTO criar(VeiculoCreateDTO dto) {

        if (veiculoRepository.existsByPlaca(dto.placa())) {
            throw new BusinessException("Já existe um veículo cadastrado com essa placa.");
        }

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        if (Boolean.FALSE.equals(cliente.getAtivo())) {
            throw new BusinessException("Não é possível cadastrar veículo para cliente inativo.");
        }

        Veiculo veiculo = Veiculo.builder()
                .placa(dto.placa())
                .modelo(dto.modelo())
                .marca(dto.marca())
                .ano(dto.ano())
                .tipo(dto.tipo())
                .cliente(cliente)
                .ativo(true)
                .build();

        Veiculo salvo = veiculoRepository.save(veiculo);

        return mapToDTO(salvo);
    }

    /**
     * Lista apenas veículos ativos de forma paginada.
     */
    public Page<VeiculoResponseDTO> listar(Pageable pageable) {
        return veiculoRepository.findAllByAtivoTrue(pageable)
                .map(VeiculoResponseDTO::new);
    }

    /**
     * Lista veículos ativos de um cliente específico.
     */
    public Page<VeiculoResponseDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        return veiculoRepository.findAllByCliente_IdAndAtivoTrue(clienteId, pageable)
                .map(VeiculoResponseDTO::new);
    }


    /**
     * Recupera um veículo pelo identificador.
     *
     * @throws NotFoundException caso o veículo não exista
     */
    public VeiculoResponseDTO buscarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));
        return mapToDTO(veiculo);
    }

    /**
     * Atualiza parcialmente um veículo existente.
     *
     * @throws NotFoundException caso o veículo não exista
     */
    @Transactional
    public VeiculoResponseDTO atualizar(Long id, VeiculoUpdateDTO dto) {

        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));

        if (dto.modelo() != null) veiculo.setModelo(dto.modelo());
        if (dto.marca() != null) veiculo.setMarca(dto.marca());
        if (dto.ano() != null) veiculo.setAno(dto.ano());
        if (dto.tipo() != null) veiculo.setTipo(dto.tipo());
        if (dto.ativo() != null) veiculo.setAtivo(dto.ativo());

        return mapToDTO(veiculoRepository.save(veiculo));
    }

    /**
     * Realiza exclusão lógica de um veículo.
     */
    @Transactional
    public void deletar(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));

        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);
    }

    /**
     * Centraliza a conversão da entidade {@link Veiculo}
     * para {@link VeiculoResponseDTO}.
     */
    private VeiculoResponseDTO mapToDTO(Veiculo veiculo) {
        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getMarca(),
                veiculo.getAno(),
                veiculo.getTipo(),
                veiculo.getCliente().getId(),
                veiculo.getAtivo()

        );
    }
}
