package br.com.ralfdomingues.oficina.domain.ordemservico.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para criação de uma Ordem de Serviço.
 *
 * <p>
 * Contém os dados necessários para registrar uma nova ordem, incluindo cliente, veículo,
 * descrição, valor estimado e status inicial.
 * </p>
 *
 */
public record OrdemServicoCreateDTO(
        @NotNull Long clienteId,
        @NotNull Long veiculoId,
        @NotBlank String descricao,
        BigDecimal valorEstimado,
        StatusOrdemServico status
) {}
