package br.com.ralfdomingues.oficina.repository.usuario;

import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repositório responsável pelo acesso aos dados de {@link Usuario}.
 *
 * <p>
 * Centraliza consultas relacionadas à autenticação, verificação de unicidade
 * e listagem de usuários ativos.
 * </p>
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     *
     * <p>
     * Utilizado principalmente no processo de autenticação.
     *
     * @param email email do usuário
     * @return usuário encontrado, se existir
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Retorna apenas usuários ativos de forma paginada.
     *
     * <p>
     * Evita a exposição de usuários desativados nas listagens da aplicação.
     *
     * @param pageable parâmetros de paginação
     * @return página de usuários ativos
     */
    Page<Usuario> findAllByAtivoTrue(Pageable pageable);

    /**
     * Verifica se já existe um usuário cadastrado com o email informado.
     *
     * <p>
     * Utilizado para garantir a unicidade do email no cadastro.
     *
     * @param email email a ser verificado
     * @return {@code true} se o email já estiver em uso
     */
    boolean existsByEmail(String email);
}
