package br.com.ralfdomingues.oficina.domain.cliente.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * Representa um cliente da oficina.
 *
 * <p>Um cliente pode possuir um ou mais veículos cadastrados e pode abrir
 * ordens de serviço. Esta entidade é fundamental para vincular atendimentos
 * e registrar o histórico de serviços realizados.</p>
 *
 * <p>Regras importantes:
 * <ul>
 *     <li>O cliente deve conter dados mínimos válidos (nome, telefone, documento).</li>
 *     <li>Pode estar associado a diferentes ordens de serviço ao longo do tempo.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "cliente",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cliente_cpf", columnNames = "cpf")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 8, max = 20)
    private String telefone;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 14)
    private String cpf;

    private String email;

    @Column(nullable = false)
    private Boolean ativo = true;

}
