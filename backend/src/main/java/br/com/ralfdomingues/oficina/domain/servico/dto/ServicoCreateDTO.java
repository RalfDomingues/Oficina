package br.com.ralfdomingues.oficina.domain.servico.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO utilizado para criação de um novo Serviço no sistema.
 *
 * <p>Responsável por transportar apenas os dados essenciais
 * informados pelo cliente da API, garantindo validações básicas
 * antes da conversão para a entidade de domínio.</p>
 */
public record ServicoCreateDTO(

        @NotBlank
        String nome,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal preco
) {}
