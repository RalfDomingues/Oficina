package br.com.ralfdomingues.oficina.domain.auth.dto;

import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;

/**
 * DTO de resposta do processo de autenticação.
 *
 * <p>
 * Contém o token JWT necessário para autenticação nas
 * requisições subsequentes, além das informações básicas
 * do usuário autenticado.
 */
public record LoginResponseDTO(
        /**
         * Token JWT que deve ser enviado no header Authorization
         * no formato: {@code Bearer <token>}.
         */
        String token,
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil
) {}

