package br.com.ralfdomingues.oficina.domain.servico.service;

import br.com.ralfdomingues.oficina.domain.servico.dto.*;
import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.servico.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

/**
 * Camada de serviço responsável pelas regras de negócio
 * relacionadas à entidade {@link Servico}.
 *
 * <p>Centraliza operações de criação, atualização, listagem
 * e exclusão lógica, garantindo consistência do domínio.</p>
 */
@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera um serviço ativo ou não pelo identificador.
     *
     * @throws NotFoundException caso o serviço não exista
     */
    public ServicoResponseDTO listarPorId(Long id) {
        var servico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));

        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getPreco(), servico.getAtivo());
    }

    /**
     * Cria um novo serviço no sistema.
     *
     * <p>O serviço é criado como ativo por padrão.</p>
     */
    @Transactional
    public ServicoResponseDTO criar(ServicoCreateDTO dto) {
        var servico = new Servico(null, dto.nome(), dto.preco());
        repository.save(servico);

        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getPreco(), servico.getAtivo());
    }

    /**
     * Atualiza parcialmente um serviço existente.
     *
     * <p>Apenas campos não nulos no DTO sobrescrevem
     * os valores atuais da entidade.</p>
     *
     * @throws NotFoundException caso o serviço não exista
     */
    @Transactional
    public ServicoResponseDTO atualizar(Long id, ServicoUpdateDTO dto) {
        var servico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));


        if (dto.nome() != null) servico.setNome(dto.nome());
        if (dto.preco() != null) servico.setPreco(dto.preco());
        if (dto.ativo() != null) servico.setAtivo(dto.ativo());

        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getPreco(), servico.getAtivo());
    }

    /**
     * Lista apenas serviços ativos de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<ServicoResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(ServicoResponseDTO::new);
    }

    /**
     * Realiza exclusão lógica de um serviço.
     *
     * <p>O registro não é removido do banco,
     * apenas marcado como inativo.</p>
     *
     * @throws NotFoundException caso o serviço não exista
     */
    @Transactional
    public void deletar(Long id) {

        Servico servico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado."));

        servico.setAtivo(false);
        repository.save(servico);
    }
}
