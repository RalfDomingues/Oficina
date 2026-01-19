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

/**
 * Controller responsável pelo gerenciamento de ordens de serviço da oficina.
 *
 * <p>
 * A ordem de serviço representa o fluxo principal do negócio,
 * conectando cliente, veículo, serviços executados e faturamento.
 *
 * <p>
 * Este controller expõe operações de criação, consulta, atualização
 * e cancelamento de ordens, delegando todas as regras de negócio
 * ao {@link OrdemServicoService}.
 */
@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    /**
     * Cria uma nova ordem de serviço.
     *
     * @param dto dados iniciais da ordem
     * @return ordem de serviço criada
     */
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criar(
            @RequestBody @Valid OrdemServicoCreateDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.criar(dto));
    }

    /**
     * Busca uma ordem de serviço pelo identificador.
     *
     * @param id identificador da ordem
     * @return ordem de serviço
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Lista ordens de serviço de forma paginada.
     *
     * <p>
     * Este endpoint é utilizado tanto para acompanhamento operacional
     * quanto para consultas administrativas.
     *
     * @param pageable parâmetros de paginação
     * @return página de ordens de serviço
     */
    @GetMapping
    public ResponseEntity<Page<OrdemServicoResponseDTO>> listar(Pageable pageable) {
        Page<OrdemServicoResponseDTO> resultado = service.listar(pageable);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Atualiza os dados de uma ordem de serviço existente.
     *
     * @param id  identificador da ordem
     * @param dto dados atualizados
     * @return ordem atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid OrdemServicoUpdateDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * Cancela uma ordem de serviço.
     *
     * <p>
     * Embora utilize o verbo DELETE, o comportamento real
     * (remoção física ou lógica) é definido na camada de serviço,
     * conforme as regras do negócio.
     *
     * @param id identificador da ordem
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
