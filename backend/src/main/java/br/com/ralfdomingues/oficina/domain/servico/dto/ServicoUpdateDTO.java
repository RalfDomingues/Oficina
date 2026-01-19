package br.com.ralfdomingues.oficina.domain.servico.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO utilizado para atualização parcial de um Serviço.
 *
 * <p>Todos os campos são opcionais e, quando informados,
 * sobrescrevem os valores atuais da entidade.</p>
 */
public record ServicoUpdateDTO(

        String nome,

        @DecimalMin("0.01")
        BigDecimal preco,

        Boolean ativo
) {}
