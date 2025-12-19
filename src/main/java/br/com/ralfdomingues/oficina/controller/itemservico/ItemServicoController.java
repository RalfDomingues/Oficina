package br.com.ralfdomingues.oficina.controller.itemservico;

import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.service.ItemServicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo gerenciamento de itens de serviço
 * associados às ordens de serviço.
 *
 * <p>
 * Um item de serviço representa a execução de um serviço específico
 * dentro de uma ordem, podendo ser criado, consultado, atualizado
 * ou removido conforme o fluxo operacional da oficina.
 *
 * <p>
 * Todas as regras de negócio e validações de relacionamento
 * com a ordem de serviço são tratadas no {@link ItemServicoService}.
 */
@RestController
@RequestMapping("/itens-servico")
public class ItemServicoController {

    private final ItemServicoService service;

    public ItemServicoController(ItemServicoService service) {
        this.service = service;
    }

    /**
     * Cria um novo item de serviço vinculado a uma ordem de serviço.
     *
     * @param dto dados do item de serviço
     * @return item criado
     */
    @PostMapping
    public ResponseEntity<ItemServicoResponseDTO> criar(
            @RequestBody @Valid ItemServicoCreateDTO dto) {

        return ResponseEntity.ok(service.criar(dto));
    }

    /**
     * Lista todos os itens de serviço de forma paginada.
     *
     * <p>
     * Este endpoint é utilizado principalmente para consultas
     * administrativas e auditoria.
     *
     * @param pageable parâmetros de paginação
     * @return página de itens de serviço
     */
    @GetMapping
    public ResponseEntity<Page<ItemServicoResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    /**
     * Busca um item de serviço pelo identificador.
     *
     * @param id identificador do item
     * @return item de serviço
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Lista os itens de serviço associados a uma ordem específica.
     *
     * <p>
     * Este endpoint reforça a relação direta entre item de serviço
     * e ordem de serviço no domínio.
     *
     * @param ordemId identificador da ordem de serviço
     * @param pageable parâmetros de paginação
     * @return itens de serviço da ordem
     */
    @GetMapping("/ordem/{ordemId}")
    public ResponseEntity<Page<ItemServicoResponseDTO>> listarPorOrdem(
            @PathVariable Long ordemId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(service.listarPorOrdem(ordemId, pageable));
    }

    /**
     * Atualiza um item de serviço existente.
     *
     * @param id  identificador do item
     * @param dto dados atualizados
     * @return item atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ItemServicoUpdateDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * Remove um item de serviço.
     *
     * <p>
     * A estratégia de remoção e seus impactos na ordem de serviço
     * são definidos na camada de serviço.
     *
     * @param id identificador do item
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
