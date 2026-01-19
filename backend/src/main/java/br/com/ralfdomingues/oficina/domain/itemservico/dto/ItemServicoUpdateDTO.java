package br.com.ralfdomingues.oficina.domain.itemservico.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

/**
 * DTO para atualização de um Item de Serviço.
 *
 * <p>
 * Permite atualizar o serviço, quantidade, valor unitário e status de ativo de um item de serviço.
 * Todos os campos são opcionais, mas quando informados, seguem validações:
 * </p>
 * <ul>
 *     <li>quantidade: maior que zero</li>
 *     <li>valorUnitario: maior que zero</li>
 * </ul>
 *
 */
public record ItemServicoUpdateDTO(
        Long servicoId,

        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantidade,

        @DecimalMin(value = "0.0", inclusive = false,
                message = "Valor unitário deve ser maior que zero")
        Double valorUnitario,
        Boolean ativo
) {}
