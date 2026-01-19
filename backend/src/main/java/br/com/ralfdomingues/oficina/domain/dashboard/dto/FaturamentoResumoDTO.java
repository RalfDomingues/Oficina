package br.com.ralfdomingues.oficina.domain.dashboard.dto;

import java.math.BigDecimal;

/**
 * DTO de resumo de faturamento.
 *
 * <p>
 * Representa o valor total faturado no período considerado
 * pelas métricas do dashboard.
 */
public record FaturamentoResumoDTO(
        BigDecimal total
) {}

