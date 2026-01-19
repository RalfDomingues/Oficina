package br.com.ralfdomingues.oficina.controller.cliente;

import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteCreateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteResponseDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteUpdateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo gerenciamento de clientes da oficina.
 *
 * <p>
 * Expõe endpoints CRUD para cadastro, consulta, atualização e remoção
 * de clientes, delegando toda a lógica de negócio ao {@link ClienteService}.
 *
 * <p>
 * Este controller mantém-se intencionalmente simples, atuando apenas
 * como camada de entrada HTTP e validação básica de dados.
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    /**
     * Cadastra um novo cliente no sistema.
     *
     * @param dto dados de criação do cliente
     * @return cliente criado
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@Valid @RequestBody ClienteCreateDTO dto) {
        ClienteResponseDTO response = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista clientes de forma paginada.
     *
     * <p>
     * A paginação é padronizada com ordenação crescente por ID,
     * garantindo previsibilidade nos resultados.
     *
     * @param pageable parâmetros de paginação
     * @return página de clientes
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    /**
     * Busca um cliente pelo identificador.
     *
     * @param id identificador do cliente
     * @return cliente encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Atualiza os dados de um cliente existente.
     *
     * @param id  identificador do cliente
     * @param dto dados atualizados
     * @return cliente atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * Remove um cliente do sistema.
     *
     * <p>
     * A estratégia de remoção (física ou lógica) é definida
     * na camada de serviço.
     *
     * @param id identificador do cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
