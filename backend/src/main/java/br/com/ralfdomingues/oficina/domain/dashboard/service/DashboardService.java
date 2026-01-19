package br.com.ralfdomingues.oficina.domain.dashboard.service;

import br.com.ralfdomingues.oficina.domain.dashboard.dto.FaturamentoResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdemStatusResumoDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.OrdensPorMesDTO;
import br.com.ralfdomingues.oficina.domain.dashboard.dto.ServicoMaisUsadoDTO;
import br.com.ralfdomingues.oficina.repository.itemservico.ItemServicoRepository;
import br.com.ralfdomingues.oficina.repository.ordemservico.OrdemServicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Serviço responsável por fornecer métricas e indicadores
 * consolidados para o dashboard da aplicação.
 *
 * <p>
 * Atua como camada de orquestração de consultas agregadas,
 * delegando a responsabilidade de cálculo e agrupamento
 * às queries especializadas dos repositórios.
 *
 * <p>
 * Não contém regras de negócio transacionais nem lógica
 * de modificação de estado.
 */
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

    /**
     * Retorna o resumo de ordens de serviço agrupadas por status.
     */
    public List<OrdemStatusResumoDTO> ordensPorStatus() {
        return ordemRepository.resumoPorStatus();
    }

    /**
     * Retorna o valor total faturado considerando as ordens registradas.
     */
    public FaturamentoResumoDTO faturamentoTotal() {
        return new FaturamentoResumoDTO(
                ordemRepository.faturamentoTotal()
        );
    }

    /**
     * Retorna os serviços mais utilizados com base nos itens de serviço.
     */
    public List<ServicoMaisUsadoDTO> servicosMaisUsados() {
        return itemRepository.servicosMaisUsados();
    }

    /**
     * Retorna o volume de ordens de serviço agrupadas por mês.
     */
    public List<OrdensPorMesDTO> ordensPorMes() {
        return ordemRepository.ordensPorMes();
    }
}
