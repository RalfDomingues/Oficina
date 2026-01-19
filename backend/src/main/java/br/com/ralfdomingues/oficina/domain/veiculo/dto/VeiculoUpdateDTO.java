package br.com.ralfdomingues.oficina.domain.veiculo.dto;

import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import jakarta.validation.constraints.*;

/**
 * DTO utilizado para atualização parcial de um veículo.
 *
 * <p>Apenas os campos informados (não nulos)
 * serão aplicados sobre a entidade existente.</p>
 */
public record VeiculoUpdateDTO(

        String modelo,

        String marca,

        @Min(1900)
        @Max(2100)
        Integer  ano,

        TipoVeiculo tipo,

        Boolean ativo
) {
}
