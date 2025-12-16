package br.com.ralfdomingues.oficina.domain.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteCreateDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,
        @NotBlank(message = "Telefone é obrigatório")
        @Size(min = 8, max = 20, message = "Telefone inválido")
        String telefone,
        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 14, message = "CPF inválido")
        String cpf,
        @Email(message = "Email inválido")
        String email
) {
}
