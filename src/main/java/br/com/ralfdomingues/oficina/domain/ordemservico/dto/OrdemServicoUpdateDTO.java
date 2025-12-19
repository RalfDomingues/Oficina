package br.com.ralfdomingues.oficina.domain.ordemservico.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import java.math.BigDecimal;

/**
 * DTO para atualização de uma Ordem de Serviço.
 *
 * <p>
 * Permite atualizar a descrição, valor final ou status de uma ordem existente.
 * Todos os campos são opcionais e atualizados apenas se fornecidos.
 * </p>
 */
public record OrdemServicoUpdateDTO(
        String descricao,
        BigDecimal valor,
        StatusOrdemServico status
) {}
