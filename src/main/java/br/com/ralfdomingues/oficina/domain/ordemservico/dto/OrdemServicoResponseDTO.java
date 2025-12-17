package br.com.ralfdomingues.oficina.domain.ordemservico.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
