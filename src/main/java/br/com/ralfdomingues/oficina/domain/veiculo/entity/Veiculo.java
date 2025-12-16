package br.com.ralfdomingues.oficina.domain.veiculo.entity;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa um veículo registrado na oficina.
 *
 * <p>Todo veículo pertence a um {@link Cliente} e pode ser utilizado para abrir
 * ordens de serviço. O veículo é parte crucial do histórico de manutenções
 * e diagnósticos realizados.</p>
 *
 * <p>Regras importantes:
 * <ul>
 *     <li>A placa deve ser única e identificar o veículo.</li>
 *     <li>Um veículo não existe sem estar vinculado a um cliente.</li>
 *     <li>O tipo do veículo define se é carro, moto etc.</li>
 * </ul>
 * </p>
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
