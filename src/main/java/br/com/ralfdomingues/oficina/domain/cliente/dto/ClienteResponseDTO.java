package br.com.ralfdomingues.oficina.domain.cliente.dto;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;

/**
 * DTO de resposta para operações envolvendo clientes.
 *
 * <p>
 * Representa a visão externa da entidade {@link Cliente},
 * expondo apenas os dados necessários para consumo da API.
 */
public record ClienteResponseDTO(
        Long id,
        String nome,
        String telefone,
        String cpf,
        String email,
        Boolean ativo
) {

    /**
     * Construtor utilitário para mapeamento direto da entidade.
     *
     * <p>
     * Centraliza a conversão {@link Cliente -> ClienteResponseDTO},
     * evitando duplicação de código na camada de serviço.
     *
     * @param c entidade cliente
     */
    public ClienteResponseDTO(Cliente c) {
        this(c.getId(), c.getNome(), c.getTelefone(), c.getCpf(), c.getEmail(), c.getAtivo());
    }
}
