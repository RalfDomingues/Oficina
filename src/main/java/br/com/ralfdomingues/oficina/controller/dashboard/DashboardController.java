package br.com.ralfdomingues.oficina.controller.dashboard;

import br.com.ralfdomingues.oficina.domain.dashboard.dto.FaturamentoResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdemStatusResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdensPorMesDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.ServicoMaisUsadoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controller responsável pela exposição de dados consolidados
 * para visualização no dashboard da aplicação.
 *
 * <p>
 * Diferente dos controllers de CRUD, este controller fornece
 * apenas consultas agregadas e métricas estratégicas, sem
 * permitir alterações no estado do sistema.
 *
 * <p>
 * Todos os cálculos, agregações e regras de negócio são
 * centralizados no {@link DashboardService}, mantendo o
 * controller como uma camada de entrega de dados.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    /**
     * Retorna um resumo da quantidade de ordens de serviço
     * agrupadas por status.
     *
     * @return lista com a distribuição de ordens por status
     */
    @GetMapping("/ordens-por-status")
    public ResponseEntity<List<OrdemStatusResumoDTO>> ordensPorStatus() {
        return ResponseEntity.ok(service.ordensPorStatus());
    }


    /**
     * Retorna o faturamento total da oficina com base
     * nas ordens de serviço finalizadas.
     *
     * @return resumo do faturamento
     */
    @GetMapping("/faturamento-total")
    public ResponseEntity<FaturamentoResumoDTO> faturamentoTotal() {
        return ResponseEntity.ok(service.faturamentoTotal());
    }

    /**
     * Retorna os serviços mais utilizados nas ordens de serviço.
     *
     * @return lista de serviços ordenados por uso
     */
    @GetMapping("/servicos-mais-usados")
    public ResponseEntity<List<ServicoMaisUsadoDTO>> servicosMaisUsados() {
        return ResponseEntity.ok(service.servicosMaisUsados());
    }

    /**
     * Retorna a quantidade de ordens de serviço agrupadas por mês.
     *
     * @return histórico mensal de ordens
     */
    @GetMapping("/ordens-por-mes")
    public ResponseEntity<List<OrdensPorMesDTO>> ordensPorMes() {
        return ResponseEntity.ok(service.ordensPorMes());
    }
}

