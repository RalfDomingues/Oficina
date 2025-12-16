package br.com.ralfdomingues.oficina.domain.ordemservico.entity;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Representa uma Ordem de Serviço (OS) dentro da oficina mecânica.
 *
 * <p>Uma OS registra o atendimento realizado para um veículo de um cliente,
 * incluindo o problema informado, status de execução, datas e valores.
 * Esta é a entidade central do sistema, pois organiza o fluxo de trabalho
 * da oficina do início ao fim.</p>
 *
 * <p>Regras importantes:
 * <ul>
 *     <li>Deve estar sempre associada a um cliente e um veículo.</li>
 *     <li>Possui um status obrigatório que controla seu fluxo.</li>
 *     <li>A data de abertura é registrada automaticamente no momento da criação.</li>
 *     <li>A data de conclusão só deve existir quando o status for CONCLUIDA.</li>
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

    public OrdemServico(Cliente cliente, Veiculo veiculo, String descricao, BigDecimal valorEstimado) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.descricao = descricao;
        this.valorEstimado = valorEstimado;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOrdemServico.ABERTA;
    }
}
