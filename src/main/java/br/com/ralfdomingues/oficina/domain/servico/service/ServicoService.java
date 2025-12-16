package br.com.ralfdomingues.oficina.domain.servico.service;

import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;
import br.com.ralfdomingues.oficina.domain.servico.dto.*;
import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.servico.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public ServicoResponseDTO listarPorId(Long id) {
        var servico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));

        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getPreco(), servico.getAtivo());
    }

    @Transactional
    public ServicoResponseDTO criar(ServicoCreateDTO dto) {
        var servico = new Servico(null, dto.nome(), dto.preco());
        repository.save(servico);

        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getPreco(), servico.getAtivo());
    }

    @Transactional
    public ServicoResponseDTO atualizar(Long id, ServicoUpdateDTO dto) {
        var servico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));


        if (dto.nome() != null) servico.setNome(dto.nome());
        if (dto.preco() != null) servico.setPreco(dto.preco());
        if (dto.ativo() != null) servico.setAtivo(dto.ativo());

        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getPreco(), servico.getAtivo());
    }

    @Transactional(readOnly = true)
    public Page<ServicoResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(ServicoResponseDTO::new);
    }


    @Transactional
    public void deletar(Long id) {

        Servico servico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado."));

        servico.setAtivo(false);
        repository.save(servico);
    }
}
