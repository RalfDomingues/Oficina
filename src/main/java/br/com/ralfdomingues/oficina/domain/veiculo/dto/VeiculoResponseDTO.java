package br.com.ralfdomingues.oficina.domain.veiculo.dto;

import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;

public record VeiculoResponseDTO(
        Long id,
        String placa,
        String modelo,
        String marca,
        Integer  ano,
        TipoVeiculo tipo,
        Long clienteId,
        Boolean ativo
) {
    public VeiculoResponseDTO(Veiculo veiculo) {
        this(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getMarca(),
                veiculo.getAno(),
                veiculo.getTipo(),
                veiculo.getCliente().getId(),
                veiculo.getAtivo()
        );
    }
}
