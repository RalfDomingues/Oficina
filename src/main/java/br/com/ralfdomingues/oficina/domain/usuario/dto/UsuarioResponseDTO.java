package br.com.ralfdomingues.oficina.domain.usuario.dto;

import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;

/**
 * DTO de resposta que representa um usuário retornado pela API.
 *
 * <p>Utilizado para expor dados de leitura ao cliente,
 * evitando o acoplamento direto com a entidade {@link Usuario}.</p>
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        Boolean ativo
) {

    /**
     * Construtor de conveniência para conversão
     * da entidade {@link Usuario} em DTO de resposta.
     */
    public UsuarioResponseDTO(Usuario u) {
        this(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getAtivo());
    }
}
