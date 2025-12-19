package br.com.ralfdomingues.oficina.domain.veiculo.entity;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de domínio que representa um veículo registrado na oficina.
 *
 * <p>O veículo está sempre associado a um {@link Cliente}
 * e é utilizado como base para abertura de ordens de serviço
 * e histórico de manutenções.</p>
 */
@Entity
@Table(name = "veiculo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String placa;

    @Column(nullable = false, length = 60)
    private String modelo;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false)
    private int ano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVeiculo tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false)
    private Boolean ativo = true;

}
