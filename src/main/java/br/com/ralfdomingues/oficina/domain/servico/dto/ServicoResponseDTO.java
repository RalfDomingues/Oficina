package br.com.ralfdomingues.oficina.domain.servico.dto;

import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import java.math.BigDecimal;

public record ServicoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        Boolean ativo
) {
    public ServicoResponseDTO(Servico servico) {
        this(
                servico.getId(),
                servico.getNome(),
                servico.getPreco(),
                servico.getAtivo()
        );
    }
}
