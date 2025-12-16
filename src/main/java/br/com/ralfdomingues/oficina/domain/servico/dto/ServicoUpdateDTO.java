package br.com.ralfdomingues.oficina.domain.servico.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ServicoUpdateDTO(

        String nome,

        @DecimalMin("0.01")
        BigDecimal preco,

        Boolean ativo
) {}
