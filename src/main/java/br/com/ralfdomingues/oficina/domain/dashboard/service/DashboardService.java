package br.com.ralfdomingues.oficina.domain.dashboard.service;

import br.com.ralfdomingues.oficina.domain.dashboard.dto.FaturamentoResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdemStatusResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdensPorMesDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.ServicoMaisUsadoDTO;
import br.com.ralfdomingues.oficina.repository.itemservico.ItemServicoRepository;
import br.com.ralfdomingues.oficina.repository.ordemservico.OrdemServicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardService {

    private final OrdemServicoRepository ordemRepository;
    private final ItemServicoRepository itemRepository;

    public DashboardService(
            OrdemServicoRepository ordemRepository,
            ItemServicoRepository itemRepository) {
        this.ordemRepository = ordemRepository;
        this.itemRepository = itemRepository;
    }

    public List<OrdemStatusResumoDTO> ordensPorStatus() {
        return ordemRepository.resumoPorStatus();
    }

    public FaturamentoResumoDTO faturamentoTotal() {
        return new FaturamentoResumoDTO(
                ordemRepository.faturamentoTotal()
        );
    }

    public List<ServicoMaisUsadoDTO> servicosMaisUsados() {
        return itemRepository.servicosMaisUsados();
    }

    // opcional
    public List<OrdensPorMesDTO> ordensPorMes() {
        return ordemRepository.ordensPorMes();
    }
}
