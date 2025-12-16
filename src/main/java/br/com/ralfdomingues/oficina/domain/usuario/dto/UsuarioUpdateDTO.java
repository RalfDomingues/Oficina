package br.com.ralfdomingues.oficina.domain.usuario.dto;

import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;

public record UsuarioUpdateDTO(

        String nome,

        @Email
        String email,

        String senha,

        PerfilUsuario perfil,

        Boolean ativo
) {}
