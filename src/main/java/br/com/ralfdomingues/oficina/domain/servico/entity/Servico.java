package br.com.ralfdomingues.oficina.domain.servico.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;


/**
 * Entidade de domínio que representa um serviço prestado pela oficina.
 *
 * <p>Modela as informações persistidas no banco de dados e
 * centraliza regras básicas de integridade do serviço.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @DecimalMin(value = "0.01")
    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Construtor de conveniência utilizado em cenários
     * onde o serviço deve ser criado como ativo por padrão.
     */
    public Servico(Long id, String nome, BigDecimal preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.ativo = true;
    }
}
