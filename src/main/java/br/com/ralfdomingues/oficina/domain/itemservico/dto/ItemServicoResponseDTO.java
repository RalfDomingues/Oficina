package br.com.ralfdomingues.oficina.domain.itemservico.dto;

import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;

import java.math.BigDecimal;

public record ItemServicoResponseDTO(
        Long id,
        Long ordemServicoId,
        Long servicoId,
        String nomeServico,
        BigDecimal valorUnitario,
        Integer quantidade,
        BigDecimal valorTotal,
        Boolean ativo
) {
    public ItemServicoResponseDTO(ItemServico item) {
        this(
                item.getId(),
                item.getOrdem().getId(),
                item.getServico().getId(),
                item.getServico().getNome(),
                item.getValorUnitario(),
                item.getQuantidade(),
                item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())),
                item.isAtivo()
        );
    }
}
