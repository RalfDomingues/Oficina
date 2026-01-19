package br.com.ralfdomingues.oficina.domain.dashboard.dto;

/**
 * DTO de resumo dos serviços mais utilizados.
 *
 * <p>
 * Representa a quantidade de ocorrências de cada serviço,
 * utilizado para métricas de uso no dashboard.
 */
public record ServicoMaisUsadoDTO(
        String nomeServico,
        Long quantidade
) {}
