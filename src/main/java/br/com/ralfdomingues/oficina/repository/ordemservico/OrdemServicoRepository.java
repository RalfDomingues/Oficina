package br.com.ralfdomingues.oficina.repository.ordemservico;

import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdemStatusResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdensPorMesDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findAllByStatusNot(StatusOrdemServico status);

    Page<OrdemServico> findAllByStatusNot(
            StatusOrdemServico status,
            Pageable pageable
    );

    Optional<OrdemServico> findByIdAndStatusNot(
            Long id,
            StatusOrdemServico status
    );

    @Query("""
                SELECT new br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdemStatusResumoDTO(
                    o.status, COUNT(o)
                )
                FROM OrdemServico o
                GROUP BY o.status
            """)
    List<OrdemStatusResumoDTO> resumoPorStatus();

    @Query("""
                SELECT COALESCE(SUM(o.valorFinal), 0)
                FROM OrdemServico o
                WHERE o.status = 'CONCLUIDA'
            """)
    BigDecimal faturamentoTotal();

    @Query("""
                SELECT new br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdensPorMesDTO(
                    CONCAT(
                        YEAR(o.dataAbertura),
                        '-',
                        CASE 
                            WHEN MONTH(o.dataAbertura) < 10 
                            THEN CONCAT('0', MONTH(o.dataAbertura))
                            ELSE CAST(MONTH(o.dataAbertura) AS string)
                        END
                    ),
                    COUNT(o)
                )
                FROM OrdemServico o
                GROUP BY YEAR(o.dataAbertura), MONTH(o.dataAbertura)
                ORDER BY YEAR(o.dataAbertura), MONTH(o.dataAbertura)
            """)
    List<OrdensPorMesDTO> ordensPorMes();


}
