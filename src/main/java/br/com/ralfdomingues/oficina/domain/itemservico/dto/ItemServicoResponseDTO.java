package br.com.ralfdomingues.oficina.domain.itemservico.dto;

import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;

import java.math.BigDecimal;

/**
 * DTO de resposta para Item de Serviço.
 *
 * <p>
 * Representa os dados de um item de serviço retornados pela API, incluindo informações da ordem,
 * serviço, quantidade e valores.
 * </p>
 */
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

    /**
     * Construtor que cria um DTO a partir de uma entidade {@link ItemServico}.
     *
     * @param item entidade de Item de Serviço
     */
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
