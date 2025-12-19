package br.com.ralfdomingues.oficina.domain.itemservico.entity;

import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidade que representa um Item de Serviço em uma Ordem de Serviço.
 *
 * <p>
 * Armazena informações do serviço realizado, quantidade, valor unitário e status de ativo.
 * </p>
 */
@Entity
@Table(name = "itens_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_id", nullable = false)
    private OrdemServico ordem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal valorUnitario;

    @Column(nullable = false)
    private boolean ativo = true;

    /**
     * Construtor parcial para criar um item de serviço com ordem, serviço, quantidade e valor unitário.
     * O status ativo é definido como true por padrão.
     */
    public ItemServico(OrdemServico ordem, Servico servico, Integer quantidade, BigDecimal valorUnitario) {
        this.ordem = ordem;
        this.servico = servico;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.ativo = true;
    }


}

