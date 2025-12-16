package br.com.ralfdomingues.oficina.domain.auth.dto;

import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;

public record LoginResponseDTO(
        String token,
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil
) {}

