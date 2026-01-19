package br.com.ralfdomingues.oficina.repository.itemservico;

import br.com.ralfdomingues.oficina.domain.dashboard.dto.ServicoMaisUsadoDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositório responsável pelo acesso a dados de {@link ItemServico}.
 *
 * <p>
 * Centraliza consultas relacionadas aos itens vinculados às ordens de serviço,
 * incluindo filtros por ordem, status ativo e consultas de apoio ao dashboard.
 */
public interface ItemServicoRepository extends JpaRepository<ItemServico, Long> {

    /**
     * Retorna todos os itens ativos vinculados a uma ordem de serviço.
     *
     * @param ordemId identificador da ordem
     * @return lista de itens ativos da ordem
     */
    List<ItemServico> findAllByOrdem_IdAndAtivoTrue(Long ordemId);

    /**
     * Retorna todos os itens de serviço ativos.
     *
     * @return lista de itens ativos
     */
    List<ItemServico> findAllByAtivoTrue();

    /**
     * Retorna itens de serviço ativos de forma paginada.
     *
     * @param pageable parâmetros de paginação
     * @return página de itens ativos
     */
    Page<ItemServico> findAllByAtivoTrue(Pageable pageable);

    /**
     * Retorna itens ativos vinculados a uma ordem de serviço, de forma paginada.
     *
     * @param ordemId identificador da ordem
     * @param pageable parâmetros de paginação
     * @return página de itens ativos da ordem
     */
    Page<ItemServico> findAllByOrdem_IdAndAtivoTrue(Long ordemId, Pageable pageable);

    /**
     * Retorna os serviços mais utilizados, ordenados por quantidade decrescente.
     *
     * <p>
     * Consulta utilizada para geração de indicadores no dashboard.
     *
     * @return lista de serviços com total de utilização
     */
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
