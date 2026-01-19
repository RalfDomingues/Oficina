package br.com.ralfdomingues.oficina.domain.ordemservico.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Ordens de Serviço.
 *
 * <p>
 * Fornece todos os detalhes de uma ordem, incluindo cliente, veículo, status, descrição,
 * valores e datas de abertura e conclusão.
 * </p>
 */
public record OrdemServicoResponseDTO(

        Long id,
        Long clienteId,
        Long veiculoId,
        StatusOrdemServico status,
        String descricao,
        BigDecimal valorFinal,
        BigDecimal valorEstimado,
        LocalDateTime dataAbertura,
        LocalDateTime dataConclusao

) {

    /**
     * Construtor que cria o DTO a partir da entidade {@link OrdemServico}.
     */
    public OrdemServicoResponseDTO(OrdemServico os) {
        this(
                os.getId(),
                os.getCliente().getId(),
                os.getVeiculo().getId(),
                os.getStatus(),
                os.getDescricao(),
                os.getValorFinal(),
                os.getValorEstimado(),
                os.getDataAbertura(),
                os.getDataConclusao()
        );
    }
}
