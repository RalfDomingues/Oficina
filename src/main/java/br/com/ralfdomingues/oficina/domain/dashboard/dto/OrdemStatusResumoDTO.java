package br.com.ralfdomingues.oficina.domain.dashboard.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;

/**
 * DTO de resumo de ordens de serviço por status.
 *
 * <p>
 * Representa o agrupamento de ordens conforme seu status atual,
 * utilizado para métricas e visualizações no dashboard.
 */
public record OrdemStatusResumoDTO(
        StatusOrdemServico status,
        Long quantidade
) {}

