package br.com.ralfdomingues.oficina.domain.usuario.dto;

import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para criação de um novo usuário no sistema.
 *
 * <p>Responsável por receber os dados iniciais de cadastro,
 * aplicando validações antes da conversão para a entidade
 * de domínio.</p>
 */
public record UsuarioCreateDTO(

        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha,

        @NotNull
        PerfilUsuario perfil
) {}
