package br.com.ralfdomingues.oficina.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para requisições de autenticação.
 *
 * <p>
 * Representa as credenciais necessárias para o processo de login.
 * As validações garantem apenas a integridade básica dos dados,
 * sem conter qualquer regra de negócio.
 */
public record LoginRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha
) {}
