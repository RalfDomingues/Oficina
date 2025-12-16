package br.com.ralfdomingues.oficina.domain.cliente.service;

import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteCreateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteResponseDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteUpdateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas a {@link Cliente}.
 *
 * <p>Esta classe centraliza operações como cadastro, atualização e busca
 * de clientes. Todas as validações específicas do domínio acontecem aqui,
 * protegendo o restante da aplicação contra estados inválidos.</p>
 *
 * <p>Principais responsabilidades:
 *     <ul>
 *         <li>Garantir unicidade de documento (CPF/CNPJ).</li>
 *         <li>Aplicar validações de negócio antes de persistir.</li>
 *         <li>Evitar duplicidade e inconsistência de dados.</li>
 *         <li>Converter entidades para DTOs de saída.</li>
 *     </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    /**
     * Cria um novo cliente após validar regras de negócio.
     *
     * @param dto dados de entrada para criação
     * @return representação do cliente criado
     * @throws BusinessException caso o documento já esteja cadastrado
     */
    @Transactional
    public ClienteResponseDTO criar(ClienteCreateDTO dto) {

        if (repository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado.");
        }

        Cliente cliente = Cliente.builder()
                .nome(dto.nome())
                .telefone(dto.telefone())
                .cpf(dto.cpf())
                .email(dto.email())
                .ativo(true)
                .build();

        repository.save(cliente);

        return new ClienteResponseDTO(cliente);
    }

    /**
     * Retorna um cliente a partir de seu ID.
     *
     * @param id identificador do cliente
     * @return DTO com os dados do cliente
     * @throws BusinessException se o cliente não existir
     */
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .filter(Cliente::getAtivo)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        return new ClienteResponseDTO(cliente);
    }

    /**
     * Atualiza campos permitidos de um cliente.
     *
     * <p>Somente valores não nulos no DTO são atualizados. Isso permite
     * operações de atualização parcial (PATCH-like behavior).</p>
     *
     * @param id identificador do cliente
     * @param dto dados a serem atualizados
     * @return DTO atualizado
     * @throws BusinessException se o cliente não existir
     * @throws BusinessException se o novo documento for duplicado
     */
    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteUpdateDTO dto) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        if (dto.nome() != null) cliente.setNome(dto.nome());
        if (dto.telefone() != null) cliente.setTelefone(dto.telefone());
        if (dto.email() != null) cliente.setEmail(dto.email());
        if (dto.ativo() != null) cliente.setAtivo(dto.ativo());

        repository.save(cliente);
        return new ClienteResponseDTO(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        cliente.setAtivo(false);
        repository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(ClienteResponseDTO::new);
    }

}
