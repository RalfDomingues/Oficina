package br.com.ralfdomingues.oficina.repository.veiculo;

import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    boolean existsByPlaca(String placa);

    List<Veiculo> findAllByAtivoTrue();
    List<Veiculo> findAllByCliente_IdAndAtivoTrue(Long clienteId);
    Page<Veiculo> findAllByAtivoTrue(Pageable pageable);

    Page<Veiculo> findAllByCliente_IdAndAtivoTrue(Long clienteId, Pageable pageable);

}