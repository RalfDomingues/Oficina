package br.com.ralfdomingues.oficina.domain.servico.dto;

import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import java.math.BigDecimal;

/**
 * DTO de resposta que representa um Serviço retornado pela API.
 *
 * <p>Utilizado para expor dados de leitura ao cliente,
 * desacoplando a entidade de domínio da camada externa.</p>
 */
public record ServicoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        Boolean ativo
) {

    /**
     * Construtor de conveniência para conversão direta
     * da entidade {@link Servico} para o DTO de resposta.
     *
     */
    public ServicoResponseDTO(Servico servico) {
        this(
                servico.getId(),
                servico.getNome(),
                servico.getPreco(),
                servico.getAtivo()
        );
    }
}
