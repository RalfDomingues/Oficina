package br.com.ralfdomingues.oficina.controller.ordemservico;

import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.service.OrdemServicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criar(
            @RequestBody @Valid OrdemServicoCreateDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrdemServicoResponseDTO>> listar(Pageable pageable) {
        Page<OrdemServicoResponseDTO> resultado = service.listar(pageable);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid OrdemServicoUpdateDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
