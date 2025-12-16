package br.com.ralfdomingues.oficina.domain.veiculo.dto;

import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import jakarta.validation.constraints.*;

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
