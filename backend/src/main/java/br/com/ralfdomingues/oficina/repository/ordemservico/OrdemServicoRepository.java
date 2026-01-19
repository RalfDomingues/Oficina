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

/**
 * Repositório responsável pelo acesso e consultas de {@link OrdemServico}.
 *
 * <p>
 * Centraliza operações de leitura relacionadas às ordens de serviço,
 * incluindo filtros por status e consultas agregadas utilizadas no dashboard.
 * </p>
 */
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    /**
     * Retorna ordens de serviço cujo status seja diferente do informado.
     *
     * <p>
     * Utilizado para ocultar ordens canceladas ou finalizadas,
     * conforme o contexto da listagem.
     *
     * @param status status a ser ignorado
     * @param pageable parâmetros de paginação
     * @return página de ordens filtradas
     */
    Page<OrdemServico> findAllByStatusNot(
            StatusOrdemServico status,
            Pageable pageable
    );

    /**
     * Retorna uma ordem de serviço pelo ID, desde que não esteja no status informado.
     *
     * <p>
     * Utilizado para evitar acesso direto a ordens canceladas ou inválidas.
     *
     * @param id identificador da ordem
     * @param status status a ser ignorado
     * @return ordem encontrada ou vazio caso não exista ou esteja no status bloqueado
     */
    Optional<OrdemServico> findByIdAndStatusNot(
            Long id,
            StatusOrdemServico status
    );

    /**
     * Retorna o total de ordens agrupadas por status.
     *
     * <p>
     * Consulta utilizada para geração de indicadores no dashboard.
     *
     * @return lista com status e quantidade de ordens
     */
    @Query("""
                SELECT new br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdemStatusResumoDTO(
                    o.status, COUNT(o)
                )
                FROM OrdemServico o
                GROUP BY o.status
            """)
    List<OrdemStatusResumoDTO> resumoPorStatus();

    /**
     * Retorna o faturamento total das ordens concluídas.
     *
     * <p>
     * Caso não existam ordens concluídas, retorna zero.
     *
     * @return valor total faturado
     */
    @Query("""
                SELECT COALESCE(SUM(o.valorFinal), 0)
                FROM OrdemServico o
                WHERE o.status = 'CONCLUIDA'
            """)
    BigDecimal faturamentoTotal();

    /**
     * Retorna a quantidade de ordens agrupadas por mês de abertura.
     *
     * <p>
     * Utilizado para análise temporal no dashboard.
     * O mês é retornado no formato {@code YYYY-MM}.
     *
     * @return lista com mês e total de ordens
     */
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
