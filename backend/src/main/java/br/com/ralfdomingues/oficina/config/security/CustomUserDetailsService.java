package br.com.ralfdomingues.oficina.config.security;

import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import br.com.ralfdomingues.oficina.repository.usuario.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação customizada do {@link UserDetailsService} responsável por
 * integrar o modelo de usuário da aplicação ao mecanismo de autenticação
 * do Spring Security.
 *
 * <p>
 * Esta classe atua como ponte entre a entidade {@link Usuario} e o modelo
 * de autenticação do framework, convertendo dados de domínio
 * (email, senha, perfil e status) em um {@link UserDetails}.
 *
 * <p>
 * Decisões importantes:
 * <ul>
 *   <li>O email é utilizado como identificador único de autenticação</li>
 *   <li>O perfil do usuário é mapeado diretamente para uma authority</li>
 *   <li>O campo {@code ativo} controla a habilitação do usuário no sistema</li>
 * </ul>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public CustomUserDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Carrega os dados do usuário a partir do email informado durante
     * o processo de autenticação.
     *
     * <p>
     * Este método é invocado automaticamente pelo Spring Security
     * tanto no login quanto na validação de requisições autenticadas
     * via JWT.
     *
     * @param email identificador do usuário (email)
     * @return representação do usuário no formato esperado pelo Spring Security
     * @throws UsernameNotFoundException caso o usuário não exista
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAtivo(), // controla se o usuário pode se autenticar
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(usuario.getPerfil().name()))
        );
    }
}
