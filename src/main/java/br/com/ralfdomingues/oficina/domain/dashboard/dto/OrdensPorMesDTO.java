package br.com.ralfdomingues.oficina.domain.dashboard.dto;


/**
 * DTO de resumo de ordens de serviço por mês.
 *
 * <p>
 * Representa a quantidade de ordens agrupadas por mês,
 * utilizado para análise temporal no dashboard.
 *
 * <p>
 * O campo {@code mes} representa o período já formatado
 * (ex: "2024-01" ou "Jan/2024"), conforme definido na camada de serviço.
 */
public record OrdensPorMesDTO(
        String mes,
        Long quantidade
) {}

