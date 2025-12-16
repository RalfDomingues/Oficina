package br.com.ralfdomingues.oficina.domain.ordemservico.dto;

import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;

public record OrdemServicoListDTO(
        Long id,
        String clienteNome,
        String veiculoModelo,
        String status
) {
    public OrdemServicoListDTO(OrdemServico os) {
        this(os.getId(),
                os.getCliente().getNome(),
                os.getVeiculo().getModelo(),
                os.getStatus().name());
    }
}
