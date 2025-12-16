package br.com.ralfdomingues.oficina.domain.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ClienteUpdateDTO(
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,
        @Size(min = 8, max = 20, message = "Telefone inválido")
        String telefone,
        @Email(message = "Email inválido")
        String email,
        Boolean ativo
) {}

