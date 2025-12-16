package br.com.ralfdomingues.oficina.domain.ordemservico.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;

import java.math.BigDecimal;

public record OrdemServicoUpdateDTO(
        String descricao,
        BigDecimal valor,
        StatusOrdemServico status
) {}
