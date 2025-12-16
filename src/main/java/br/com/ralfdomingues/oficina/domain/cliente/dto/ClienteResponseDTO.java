package br.com.ralfdomingues.oficina.domain.cliente.dto;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String telefone,
        String cpf,
        String email,
        Boolean ativo
) {
    public ClienteResponseDTO(Cliente c) {
        this(c.getId(), c.getNome(), c.getTelefone(), c.getCpf(), c.getEmail(), c.getAtivo());
    }
}
