package br.com.ralfdomingues.oficina.domain.itemservico.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para criação de um Item de Serviço.
 *
 * <p>
 * Contém os dados necessários para adicionar um novo item a uma ordem de serviço.
 * </p>
 *
 */
public record ItemServicoCreateDTO(
        @NotNull
        Long ordemServicoId,

        @NotNull
        Long servicoId,

        @NotNull
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantidade
) {}
