package br.com.ralfdomingues.oficina.repository.cliente;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    boolean existsByCpf(String cpf);
    Page<Cliente> findAllByAtivoTrue(Pageable pageable);

}
