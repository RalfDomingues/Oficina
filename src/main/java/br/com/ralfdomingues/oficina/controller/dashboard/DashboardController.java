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


@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/ordens-por-status")
    public ResponseEntity<List<OrdemStatusResumoDTO>> ordensPorStatus() {
        return ResponseEntity.ok(service.ordensPorStatus());
    }

    @GetMapping("/faturamento-total")
    public ResponseEntity<FaturamentoResumoDTO> faturamentoTotal() {
        return ResponseEntity.ok(service.faturamentoTotal());
    }

    @GetMapping("/servicos-mais-usados")
    public ResponseEntity<List<ServicoMaisUsadoDTO>> servicosMaisUsados() {
        return ResponseEntity.ok(service.servicosMaisUsados());
    }

    @GetMapping("/ordens-por-mes")
    public ResponseEntity<List<OrdensPorMesDTO>> ordensPorMes() {
        return ResponseEntity.ok(service.ordensPorMes());
    }
}

