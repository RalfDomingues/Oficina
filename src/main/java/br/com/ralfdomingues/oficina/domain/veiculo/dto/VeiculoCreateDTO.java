package br.com.ralfdomingues.oficina.domain.veiculo.dto;

import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import jakarta.validation.constraints.*;

/**
 * DTO utilizado para criação de um veículo no sistema.
 *
 * <p>Responsável por receber os dados iniciais do veículo
 * e associá-lo a um cliente existente.</p>
 */
public record VeiculoCreateDTO(

        @NotBlank
        @Pattern(
                regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
                message = "Placa inválida (padrão Mercosul)"
        )
        String placa,

        @NotBlank
        String modelo,

        @NotBlank
        String marca,

        @Min(1900)
        @Max(2100)
        Integer  ano,

        @NotNull
        TipoVeiculo tipo,

        @NotNull
        Long clienteId
) {}
