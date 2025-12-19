package br.com.ralfdomingues.oficina.repository.cliente;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório responsável pelo acesso a dados de {@link Cliente}.
 *
 * <p>
 * Centraliza operações de persistência e consultas específicas
 * relacionadas ao ciclo de vida do cliente.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    /**
     * Verifica a existência de cliente a partir do CPF.
     *
     * <p>
     * Utilizado para garantir unicidade antes do cadastro.
     *
     * @param cpf documento do cliente
     * @return {@code true} se já existir cliente com o CPF informado
     */
    boolean existsByCpf(String cpf);

    /**
     * Retorna apenas clientes ativos de forma paginada.
     *
     * @param pageable parâmetros de paginação
     * @return página de clientes ativos
     */
    Page<Cliente> findAllByAtivoTrue(Pageable pageable);

}
