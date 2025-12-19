package br.com.ralfdomingues.oficina.domain.ordemservico.service;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import br.com.ralfdomingues.oficina.repository.itemservico.ItemServicoRepository;
import br.com.ralfdomingues.oficina.repository.ordemservico.OrdemServicoRepository;
import br.com.ralfdomingues.oficina.repository.veiculo.VeiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Serviço que gerencia o ciclo de vida das Ordens de Serviço (OS).
 *
 * <p>
 * Responsável por criar, buscar, listar, atualizar e cancelar OS, garantindo que todas as
 * regras do fluxo da oficina sejam respeitadas, como validação de cliente/veículo, status,
 * datas e valores.
 * </p>
 */
@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    private final ItemServicoRepository itemRepository;

    public OrdemServicoService(OrdemServicoRepository ordemRepository,
                               ClienteRepository clienteRepository,
                               VeiculoRepository veiculoRepository,
                               ItemServicoRepository itemRepository) {
        this.ordemRepository = ordemRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Cria uma nova OS com status ABERTA.
     *
     * @throws NotFoundException se cliente ou veículo não existirem
     * @throws BusinessException se cliente estiver inativo ou veículo não pertencer ao cliente
     */
    @Transactional
    public OrdemServicoResponseDTO criar(OrdemServicoCreateDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado."));

        Veiculo veiculo = veiculoRepository.findById(dto.veiculoId())
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));

        if (Boolean.FALSE.equals(cliente.getAtivo())) {
            throw new BusinessException("Não é possível criar OS para cliente inativo.");
        }

        if (!veiculo.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("Veículo não pertence ao cliente informado.");
        }

        OrdemServico ordem = new OrdemServico(cliente, veiculo, dto.descricao(), dto.valorEstimado());

        ordemRepository.save(ordem);
        return new OrdemServicoResponseDTO(ordem);
    }


    /** Busca uma OS pelo ID, ignorando canceladas. */
    public OrdemServicoResponseDTO buscarPorId(Long id) {
        OrdemServico ordem = ordemRepository
                .findByIdAndStatusNot(id, StatusOrdemServico.CANCELADA)
                .orElseThrow(() -> new NotFoundException("Ordem de Serviço não encontrada."));
        return new OrdemServicoResponseDTO(ordem);
    }

    /** Lista todas as OS não canceladas com paginação. */
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> listar(Pageable pageable) {
        return ordemRepository
                .findAllByStatusNot(StatusOrdemServico.CANCELADA, pageable)
                .map(OrdemServicoResponseDTO::new);
    }


    /**
     * Atualiza campos de uma OS.
     *
     * <p>
     * Regras principais:
     * <ul>
     *   <li>Atualizações são parciais (somente campos fornecidos no DTO).</li>
     *   <li>Status CONCLUIDA registra a data de conclusão automaticamente.</li>
     *   <li>Não é possível concluir sem serviços cadastrados ou sem valor final.</li>
     * </ul>
     * </p>
     *
     * @throws NotFoundException se a OS não existir
     * @throws BusinessException se regras de conclusão não forem atendidas
     */
    @Transactional
    public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoUpdateDTO dto) {

        OrdemServico ordem = ordemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordem de Serviço não encontrada."));

        if (dto.descricao() != null) {
            ordem.setDescricao(dto.descricao());
        }

        if (dto.valor() != null) {
            ordem.setValorFinal(dto.valor());
        }

        if (dto.status() != null) {

            StatusOrdemServico novoStatus = dto.status();
            StatusOrdemServico statusAtual = ordem.getStatus();

            if (novoStatus == StatusOrdemServico.CONCLUIDA) {

                boolean temItens =
                        !itemRepository.findAllByOrdem_IdAndAtivoTrue(ordem.getId()).isEmpty();

                if (!temItens) {
                    throw new BusinessException("Não é possível concluir a OS sem serviços cadastrados.");
                }

                if (ordem.getValorFinal() == null && dto.valor() == null) {
                    throw new BusinessException("Para concluir a OS, o valor final deve ser informado.");
                }

                if (statusAtual != StatusOrdemServico.CONCLUIDA) {
                    ordem.setDataConclusao(LocalDateTime.now());
                }

            } else {
                ordem.setDataConclusao(null);
            }

            ordem.setStatus(novoStatus);
        }

        ordemRepository.save(ordem);
        return new OrdemServicoResponseDTO(ordem);
    }

    /**
     * Cancela uma OS (marca como CANCELADA) e registra a data de conclusão.
     *
     * @throws NotFoundException se a OS não existir
     * @throws BusinessException se a OS já estiver CONCLUIDA
     */
    @Transactional
    public void deletar(Long id) {
        OrdemServico ordem = ordemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordem de Serviço não encontrada."));

        if (ordem.getStatus() == StatusOrdemServico.CONCLUIDA) {
            throw new BusinessException("Não é possível excluir uma OS concluída.");
        }

        ordem.setStatus(StatusOrdemServico.CANCELADA);
        ordem.setDataConclusao(LocalDateTime.now());

        ordemRepository.save(ordem);
    }

}
