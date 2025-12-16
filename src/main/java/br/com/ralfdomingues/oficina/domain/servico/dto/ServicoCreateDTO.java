package br.com.ralfdomingues.oficina.domain.servico.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ServicoCreateDTO(

        @NotBlank
        String nome,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal preco
) {}
