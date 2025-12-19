package br.com.ralfdomingues.oficina.domain.cliente.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * Entidade que representa um cliente da oficina.
 *
 * <p>
 * O cliente é responsável por vincular veículos e ordens de serviço,
 * permitindo o registro e acompanhamento do histórico de atendimentos.
 *
 * <p>
 * A ativação/desativação permite preservar o histórico sem exclusão física.
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
