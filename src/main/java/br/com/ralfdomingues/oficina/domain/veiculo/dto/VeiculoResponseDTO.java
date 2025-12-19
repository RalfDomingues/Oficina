package br.com.ralfdomingues.oficina.domain.veiculo.dto;

import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;

/**
 * DTO de resposta que representa um veículo retornado pela API.
 *
 * <p>Utilizado para expor dados de leitura,
 * desacoplando a entidade {@link Veiculo} da camada externa.</p>
 */
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
    /**
     * Construtor de conveniência para conversão
     * da entidade {@link Veiculo} em DTO de resposta.
     */
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
