package br.com.ralfdomingues.oficina.controller.veiculo;

import br.com.ralfdomingues.oficina.domain.veiculo.dto.*;
import br.com.ralfdomingues.oficina.domain.veiculo.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo gerenciamento de veículos da oficina.
 *
 * <p>
 * O veículo representa o bem atendido pela oficina e está sempre
 * associado a um cliente. Ele funciona como ponto central para
 * abertura e acompanhamento de ordens de serviço.
 *
 * <p>
 * As operações expostas neste controller fazem parte do fluxo
 * administrativo e operacional da aplicação.
 */
@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService service;

    /**
     * Cadastra um novo veículo associado a um cliente.
     *
     * <p>
     * A validação do vínculo com o cliente é tratada
     * na camada de serviço.
     *
     * @param dto dados do veículo
     * @return veículo criado
     */
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> criar(@Valid @RequestBody VeiculoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    /**
     * Lista veículos de forma paginada.
     *
     * <p>
     * Utilizado principalmente para consultas administrativas
     * e seleção de veículos em fluxos operacionais.
     *
     * @param pageable parâmetros de paginação
     * @return página de veículos
     */
    @GetMapping
    public ResponseEntity<Page<VeiculoResponseDTO>> listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    /**
     * Lista veículos pertencentes a um cliente específico.
     *
     * <p>
     * Este endpoint facilita o fluxo de abertura de ordens
     * de serviço a partir do cliente.
     *
     * @param clienteId identificador do cliente
     * @param pageable  parâmetros de paginação
     * @return página de veículos do cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<VeiculoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId, pageable));
    }

    /**
     * Busca um veículo pelo identificador.
     *
     * @param id identificador do veículo
     * @return veículo encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Atualiza os dados de um veículo existente.
     *
     * <p>
     * Alterações não impactam ordens de serviço já concluídas,
     * preservando o histórico operacional.
     *
     * @param id  identificador do veículo
     * @param dto dados atualizados
     * @return veículo atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * Remove um veículo do sistema.
     *
     * <p>
     * A exclusão é permitida apenas quando não há
     * vínculos ativos que comprometam o histórico.
     *
     * @param id identificador do veículo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
