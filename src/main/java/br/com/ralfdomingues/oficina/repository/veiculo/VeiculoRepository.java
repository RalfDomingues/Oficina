package br.com.ralfdomingues.oficina.repository.veiculo;

import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório responsável pelo acesso aos dados de {@link Veiculo}.
 *
 * <p>
 * Centraliza consultas relacionadas à unicidade de placa, controle de veículos
 * ativos e vínculo com clientes.
 * </p>
 */
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    /**
     * Verifica se já existe um veículo cadastrado com a placa informada.
     *
     * <p>
     * Utilizado para garantir a unicidade da placa no sistema.
     *
     * @param placa placa do veículo
     * @return {@code true} se a placa já estiver cadastrada
     */
    boolean existsByPlaca(String placa);

    /**
     * Retorna todos os veículos ativos.
     *
     * <p>
     * Usado principalmente em cenários internos sem paginação.
     *
     * @return lista de veículos ativos
     */
    List<Veiculo> findAllByAtivoTrue();

    /**
     * Retorna veículos ativos de forma paginada.
     *
     * @param pageable parâmetros de paginação
     * @return página de veículos ativos
     */
    Page<Veiculo> findAllByAtivoTrue(Pageable pageable);

    /**
     * Retorna veículos ativos associados a um cliente específico.
     *
     * <p>
     * Permite listar apenas os veículos pertencentes ao cliente informado.
     *
     * @param clienteId identificador do cliente
     * @param pageable parâmetros de paginação
     * @return página de veículos do cliente
     */
    Page<Veiculo> findAllByCliente_IdAndAtivoTrue(Long clienteId, Pageable pageable);

}