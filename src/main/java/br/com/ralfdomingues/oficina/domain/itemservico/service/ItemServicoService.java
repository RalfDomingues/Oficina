package br.com.ralfdomingues.oficina.domain.itemservico.service;

import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.itemservico.ItemServicoRepository;
import br.com.ralfdomingues.oficina.repository.ordemservico.OrdemServicoRepository;
import br.com.ralfdomingues.oficina.repository.servico.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class ItemServicoService {

    private final ItemServicoRepository itemRepo;
    private final ServicoRepository servicoRepo;
    private final OrdemServicoRepository ordemRepo;

    public ItemServicoService(ItemServicoRepository itemRepo,
                              ServicoRepository servicoRepo,
                              OrdemServicoRepository ordemRepo) {
        this.itemRepo = itemRepo;
        this.servicoRepo = servicoRepo;
        this.ordemRepo = ordemRepo;
    }

    @Transactional
    public ItemServicoResponseDTO criar(ItemServicoCreateDTO dto) {

        OrdemServico ordem = ordemRepo.findById(dto.ordemServicoId())
                .orElseThrow(() -> new NotFoundException("Ordem de Serviço não encontrada."));

        Servico servico = servicoRepo.findById(dto.servicoId())
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado."));

        ItemServico item = new ItemServico(
                ordem,
                servico,
                dto.quantidade(),
                servico.getPreco()
        );

        itemRepo.save(item);
        recalcularTotal(ordem);

        return new ItemServicoResponseDTO(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemServicoResponseDTO> listarTodos(Pageable pageable) {
        return itemRepo.findAllByAtivoTrue(pageable)
                .map(ItemServicoResponseDTO::new);
    }


    @Transactional(readOnly = true)
    public ItemServicoResponseDTO buscarPorId(Long id) {
        ItemServico item = itemRepo.findById(id).filter(ItemServico::isAtivo)
                .orElseThrow(() -> new NotFoundException("Item de serviço não encontrado."));
        return new ItemServicoResponseDTO(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemServicoResponseDTO> listarPorOrdem(Long ordemId, Pageable pageable) {
        return itemRepo.findAllByOrdem_IdAndAtivoTrue(ordemId, pageable)
                .map(ItemServicoResponseDTO::new);
    }

    @Transactional
    public ItemServicoResponseDTO atualizar(Long id, ItemServicoUpdateDTO dto) {

        ItemServico item = itemRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de serviço não encontrado."));

        if (!item.isAtivo() && (dto.ativo() == null || !dto.ativo())) {
            throw new BusinessException("Não é possível atualizar um item de serviço inativo.");
        }

        if (dto.servicoId() != null) {
            Servico servico = servicoRepo.findById(dto.servicoId())
                    .orElseThrow(() -> new NotFoundException("Serviço não encontrado."));
            item.setServico(servico);
            item.setValorUnitario(servico.getPreco());
        }

        if (dto.quantidade() != null) {
            item.setQuantidade(dto.quantidade());
        }

        if (dto.valorUnitario() != null) {
            item.setValorUnitario(BigDecimal.valueOf(dto.valorUnitario()));
        }

        if (dto.ativo() != null) {
            item.setAtivo(dto.ativo());
        }

        recalcularTotal(item.getOrdem());
        return new ItemServicoResponseDTO(item);
    }


    @Transactional
    public void deletar(Long id) {

        ItemServico item = itemRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de serviço não encontrado."));

        OrdemServico ordem = item.getOrdem();

        item.setAtivo(false);
        itemRepo.save(item);

        recalcularTotal(ordem);
    }

    private void recalcularTotal(OrdemServico ordem) {

        BigDecimal total = itemRepo.findAllByOrdem_IdAndAtivoTrue(ordem.getId())
                .stream()
                .map(item -> item.getValorUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ordem.setValorFinal(total);
        ordemRepo.save(ordem);
    }
}
