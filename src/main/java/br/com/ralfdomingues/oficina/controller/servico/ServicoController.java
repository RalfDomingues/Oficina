package br.com.ralfdomingues.oficina.controller.servico;

import br.com.ralfdomingues.oficina.domain.servico.service.ServicoService;
import br.com.ralfdomingues.oficina.domain.servico.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo gerenciamento do catálogo de serviços
 * oferecidos pela oficina.
 *
 * <p>
 * Um serviço representa uma definição administrativa (descrição, valor,
 * status), sendo utilizado posteriormente na composição das ordens
 * de serviço. Ele não representa a execução em si.
 *
 * <p>
 * Todas as regras de negócio, validações e impactos em ordens de serviço
 * são tratadas no {@link ServicoService}.
 */
@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;

    /**
     * Cadastra um novo serviço no catálogo da oficina.
     *
     * @param dto dados de criação do serviço
     * @return serviço criado
     */
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    /**
     * Lista os serviços cadastrados de forma paginada.
     *
     * @param pageable parâmetros de paginação
     * @return página de serviços
     */
    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    /**
     * Busca um serviço pelo identificador.
     *
     * @param id identificador do serviço
     * @return serviço encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorId(id));
    }

    /**
     * Atualiza os dados de um serviço existente.
     *
     * @param id  identificador do serviço
     * @param dto dados atualizados
     * @return serviço atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicoUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * Remove um serviço do catálogo.
     *
     * <p>
     * A estratégia de remoção e seus impactos em ordens de serviço
     * existentes são definidos na camada de serviço.
     *
     * @param id identificador do serviço
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
