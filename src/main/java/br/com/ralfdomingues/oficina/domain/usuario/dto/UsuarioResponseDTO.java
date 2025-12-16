package br.com.ralfdomingues.oficina.domain.usuario.dto;

import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        Boolean ativo
) {
    public UsuarioResponseDTO(Usuario u) {
        this(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getAtivo());
    }
}
