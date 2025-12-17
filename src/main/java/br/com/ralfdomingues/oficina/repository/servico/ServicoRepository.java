package br.com.ralfdomingues.oficina.repository.servico;

import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    boolean existsByNomeIgnoreCase(String nome);
    List<Servico> findAllByAtivoTrue();
    Page<Servico> findAllByAtivoTrue(Pageable pageable);

}
