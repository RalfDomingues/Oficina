package br.com.ralfdomingues.oficina.domain.itemservico.dto;

import jakarta.validation.constraints.*;

public record ItemServicoCreateDTO(
        @NotNull
        Long ordemServicoId,

        @NotNull
        Long servicoId,

        @NotNull
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantidade
) {}
