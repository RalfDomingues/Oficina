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

@RestController
@RequestMapping("/itens-servico")
public class ItemServicoController {

    private final ItemServicoService service;

    public ItemServicoController(ItemServicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemServicoResponseDTO> criar(
            @RequestBody @Valid ItemServicoCreateDTO dto) {

        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ItemServicoResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(service.listarTodos(pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/ordem/{ordemId}")
    public ResponseEntity<Page<ItemServicoResponseDTO>> listarPorOrdem(
            @PathVariable Long ordemId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(service.listarPorOrdem(ordemId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ItemServicoUpdateDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
