package br.com.ralfdomingues.oficina.domain.usuario.dto;

import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;

/**
 * DTO utilizado para atualização parcial de um usuário.
 *
 * <p>Apenas os campos informados (não nulos) serão aplicados
 * sobre a entidade existente.</p>
 */
public record UsuarioUpdateDTO(

        String nome,

        @Email
        String email,

        String senha,

        PerfilUsuario perfil,

        Boolean ativo
) {}
