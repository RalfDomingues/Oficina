package br.com.ralfdomingues.oficina.repository.servico;

import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório responsável pelo acesso aos dados de {@link Servico}.
 *
 * <p>
 * Fornece consultas utilizadas para listar serviços, com opção de:
 * </p>
 */
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    /**
     * Retorna todos os serviços ativos.
     *
     * <p>
     * Utilizado em cenários onde não há necessidade de paginação,
     * como seleções internas ou associações.
     *
     * @return lista de serviços ativos
     */
    List<Servico> findAllByAtivoTrue();

    /**
     * Retorna serviços ativos de forma paginada.
     *
     * <p>
     * Utilizado em listagens expostas via API.
     *
     * @param pageable parâmetros de paginação
     * @return página de serviços ativos
     */
    Page<Servico> findAllByAtivoTrue(Pageable pageable);

    /**
     * Retorna TODOS os serviços (ativos e inativos) de forma paginada.
     *
     * <p>
     * Herdado de {@link JpaRepository}.
     * Utilizado para listar serviços incluindo inativos.
     *
     * @param pageable parâmetros de paginação
     * @return página de todos os serviços
     */
    @Override
    Page<Servico> findAll(Pageable pageable);

}
