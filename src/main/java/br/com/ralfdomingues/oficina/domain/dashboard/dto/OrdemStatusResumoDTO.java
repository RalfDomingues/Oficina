package br.com.ralfdomingues.oficina.domain.dashboard.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;

public record OrdemStatusResumoDTO(
        StatusOrdemServico status,
        Long quantidade
) {}

