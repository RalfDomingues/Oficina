package br.com.ralfdomingues.oficina.domain.usuario.entity;

import br.com.ralfdomingues.oficina.domain.usuario.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;


/**
 * Entidade de domínio que representa um usuário do sistema.
 *
 * <p>Responsável por armazenar dados de autenticação,
 * autorização e estado do usuário.</p>
 */
@Entity
@Table(
        name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Senha do usuário armazenada de forma criptografada (bcrypt).
     */
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private Boolean ativo = true;
}
