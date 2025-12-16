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

import java.util.List;


/**
 * Serviço responsável pelas regras de negócio relacionadas à entidade {@link Veiculo}.
 *
 * <p>Centraliza operações de cadastro, atualização e consulta de veículos.
 * Também garante que regras essenciais sejam respeitadas — como a unicidade da
 * placa e a associação obrigatória a um cliente.</p>
 *
 * <p>Responsabilidades principais:
 * <ul>
 *     <li>Validar existência do cliente antes de cadastrar um veículo.</li>
 *     <li>Garantir que uma placa não seja cadastrada duas vezes.</li>
 *     <li>Controlar atualizações parciais do veículo.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    /**
     * Cria um novo veículo associado a um cliente existente.
     *
     * @param dto dados de entrada para criação
     * @return representação do veículo criado
     * @throws NotFoundException se o cliente informado não existir
     * @throws BusinessException se a placa já estiver cadastrada
     */
    @Transactional
    public VeiculoResponseDTO criar(VeiculoCreateDTO dto) {

        // Regra: placa deve ser única
        if (veiculoRepository.existsByPlaca(dto.placa())) {
            throw new BusinessException("Já existe um veículo cadastrado com essa placa.");
        }

        // Regra: o veículo não pode existir sem um cliente
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        // Regra: o veículo não pode existir sem um cliente válido
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
                .build();

        Veiculo salvo = veiculoRepository.save(veiculo);

        return mapToDTO(salvo);
    }

    public Page<VeiculoResponseDTO> listar(Pageable pageable) {
        return veiculoRepository.findAllByAtivoTrue(pageable)
                .map(VeiculoResponseDTO::new);
    }

    public Page<VeiculoResponseDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        return veiculoRepository.findAllByCliente_IdAndAtivoTrue(clienteId, pageable)
                .map(VeiculoResponseDTO::new);
    }


    /**
     * Retorna um veículo pelo seu ID.
     *
     * @param id identificador do veículo
     * @return DTO com dados do veículo
     * @throws NotFoundException se o veículo não existir
     */
    public VeiculoResponseDTO buscarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));
        return mapToDTO(veiculo);
    }

    /**
     * Atualiza informações do veículo.
     *
     * <p>Apenas campos não nulos no DTO serão atualizados. Isso evita sobrescrever
     * dados desnecessariamente e permite atualizações parciais.</p>
     *
     * @param id  identificador do veículo
     * @param dto dados a serem atualizados
     * @return DTO atualizado
     * @throws NotFoundException se o veículo não existir
     * @throws BusinessException se a nova placa já estiver cadastrada
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

    @Transactional
    public void deletar(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));

        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);
    }

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
