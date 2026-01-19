package br.com.ralfdomingues.oficina.domain.ordemservico.entity;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Entidade que representa uma Ordem de Serviço (OS).
 *
 * <p>
 * Registra o atendimento realizado para um veículo de um cliente, incluindo descrição do serviço,
 * datas, status e valores. Centraliza o fluxo de trabalho da oficina do início ao fim.
 * </p>
 *
 * <p>Regras importantes:
 * <ul>
 *     <li>Deve estar associada a um cliente e um veículo.</li>
 *     <li>Status obrigatório controla o fluxo da ordem.</li>
 *     <li>Data de abertura é definida automaticamente na criação.</li>
 *     <li>Data de conclusão é preenchida apenas quando o status é CONCLUIDA.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "ordens_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(nullable = false, length = 300)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrdemServico status;

    private BigDecimal valorEstimado;

    private BigDecimal valorFinal;

    /**
     * Cria uma ordem de serviço com status ABERTA e data de abertura atual.
     */
    public OrdemServico(Cliente cliente, Veiculo veiculo, String descricao, BigDecimal valorEstimado) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.descricao = descricao;
        this.valorEstimado = valorEstimado;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOrdemServico.ABERTA;
    }
}
