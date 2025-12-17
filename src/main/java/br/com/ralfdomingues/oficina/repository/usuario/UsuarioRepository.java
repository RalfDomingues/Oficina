package br.com.ralfdomingues.oficina.repository.usuario;

import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findAllByAtivoTrue(Pageable pageable);

    boolean existsByEmail(String email);
}
