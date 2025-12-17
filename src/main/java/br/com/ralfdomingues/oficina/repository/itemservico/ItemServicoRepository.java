package br.com.ralfdomingues.oficina.repository.itemservico;

import br.com.ralfdomingues.oficina.domain.dashboard.dto.ServicoMaisUsadoDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemServicoRepository extends JpaRepository<ItemServico, Long> {
    List<ItemServico> findAllByOrdem_IdAndAtivoTrue(Long ordemId);

    List<ItemServico> findAllByAtivoTrue();

    Page<ItemServico> findAllByAtivoTrue(Pageable pageable);

    Page<ItemServico> findAllByOrdem_IdAndAtivoTrue(Long ordemId, Pageable pageable);

    @Query("""
                SELECT new br.com.ralfdomingues.oficina.domain.dashboard.dto.ServicoMaisUsadoDTO(
                    s.nome, COUNT(i)
                )
                FROM ItemServico i
                JOIN i.servico s
                GROUP BY s.nome
                ORDER BY COUNT(i) DESC
            """)
    List<ServicoMaisUsadoDTO> servicosMaisUsados();

}
