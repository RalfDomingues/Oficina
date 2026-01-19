package br.com.ralfdomingues.oficina.domain.ordemservico.enums;

/**
 * Enum que representa os possíveis status de uma Ordem de Serviço.
 *
 * <p>
 * Controla o fluxo da ordem dentro da oficina, desde a abertura até a conclusão ou cancelamento.
 * </p>
 */
public enum StatusOrdemServico {
    ABERTA,
    EM_ANDAMENTO,
    CONCLUIDA,
    CANCELADA
}
