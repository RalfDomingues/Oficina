package br.com.ralfdomingues.oficina.domain.itemservico.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

public record ItemServicoUpdateDTO(
        Long servicoId,

        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantidade,

        @DecimalMin(value = "0.0", inclusive = false,
                message = "Valor unitário deve ser maior que zero")
        Double valorUnitario,
        Boolean ativo
) {}
