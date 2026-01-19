package br.com.ralfdomingues.oficina.domain.itemservico.service;

import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.itemservico.ItemServicoRepository;
import br.com.ralfdomingues.oficina.repository.ordemservico.OrdemServicoRepository;
import br.com.ralfdomingues.oficina.repository.servico.ServicoRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServicoServiceTest {

    @Mock private ItemServicoRepository itemRepo;
    @Mock private ServicoRepository servicoRepo;
    @Mock private OrdemServicoRepository ordemRepo;

    @InjectMocks
    private ItemServicoService service;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void criar_ordemNaoEncontrada() {
        when(ordemRepo.findById(1L)).thenReturn(Optional.empty());
        var dto = new ItemServicoCreateDTO(1L, 2L, 1);
        assertThrows(NotFoundException.class, () -> service.criar(dto));
    }

    @Test
    void criar_servicoNaoEncontrado() {
        OrdemServico ordem = new OrdemServico();
        ordem.setId(1L);

        when(ordemRepo.findById(1L)).thenReturn(Optional.of(ordem));
        when(servicoRepo.findById(2L)).thenReturn(Optional.empty());

        var dto = new ItemServicoCreateDTO(1L, 2L, 1);
        assertThrows(NotFoundException.class, () -> service.criar(dto));
    }

    @Test
    void criar_sucesso() {
        OrdemServico ordem = new OrdemServico();
        ordem.setId(1L);

        Servico servico = new Servico(2L, "Troca óleo", BigDecimal.valueOf(50));

        when(ordemRepo.findById(1L)).thenReturn(Optional.of(ordem));
        when(servicoRepo.findById(2L)).thenReturn(Optional.of(servico));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(1L)).thenReturn(List.of());

        var dto = new ItemServicoCreateDTO(1L, 2L, 3);
        var resposta = service.criar(dto);

        assertEquals(BigDecimal.valueOf(150), resposta.valorTotal());
        verify(itemRepo).save(any(ItemServico.class));
        verify(ordemRepo).save(ordem);
    }

    @Test
    void listarTodos_ok() {
        OrdemServico ordem = new OrdemServico();
        Servico servico = new Servico(1L, "X", BigDecimal.ONE);
        ItemServico item = new ItemServico(ordem, servico, 1, BigDecimal.ONE);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemServico> page = new PageImpl<>(List.of(item));

        when(itemRepo.findAllByAtivoTrue(pageable)).thenReturn(page);

        Page<?> resposta = service.listarTodos(pageable);

        assertEquals(1, resposta.getTotalElements());
    }


    @Test
    void buscarPorId_naoEncontrado() {
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.buscarPorId(1L));
    }

    @Test
    void buscarPorId_inativo() {
        ItemServico item = mock(ItemServico.class);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(item.isAtivo()).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.buscarPorId(1L));
    }

    @Test
    void listarPorOrdem_ok() {
        OrdemServico ordem = new OrdemServico();
        Servico servico = new Servico(1L, "X", BigDecimal.ONE);
        ItemServico item = new ItemServico(ordem, servico, 1, BigDecimal.ONE);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemServico> page = new PageImpl<>(List.of(item));

        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(5L, pageable))
                .thenReturn(page);

        Page<?> resposta = service.listarPorOrdem(5L, pageable);

        assertEquals(1, resposta.getTotalElements());
    }


    @Test
    void atualizar_itemNaoEncontrado() {
        when(itemRepo.findById(10L)).thenReturn(Optional.empty());
        var dto = new ItemServicoUpdateDTO(null, null, null, null);
        assertThrows(NotFoundException.class, () -> service.atualizar(10L, dto));
    }

    @Test
    void atualizar_itemInativo_semReativar_lancaBusiness() {
        OrdemServico ordem = new OrdemServico();
        Servico servico = new Servico(1L, "X", BigDecimal.ONE);

        ItemServico item = new ItemServico(ordem, servico, 1, BigDecimal.ONE);
        item.setAtivo(false);

        when(itemRepo.findById(10L)).thenReturn(Optional.of(item));

        var dto = new ItemServicoUpdateDTO(5L, null, null, null);

        assertThrows(BusinessException.class, () -> service.atualizar(10L, dto));
    }


    @Test
    void atualizar_trocaServico_recalcula() {
        OrdemServico ordem = new OrdemServico();
        ordem.setId(1L);

        Servico antigo = new Servico(1L, "A", BigDecimal.valueOf(20));
        Servico novo = new Servico(2L, "B", BigDecimal.valueOf(50));

        ItemServico item = new ItemServico(ordem, antigo, 2, BigDecimal.valueOf(40));

        when(itemRepo.findById(30L)).thenReturn(Optional.of(item));
        when(servicoRepo.findById(2L)).thenReturn(Optional.of(novo));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(1L)).thenReturn(List.of(item));

        var dto = new ItemServicoUpdateDTO(null, 2, null, null);

        var resposta = service.atualizar(30L, dto);

        assertEquals(BigDecimal.valueOf(80), resposta.valorTotal());
    }

    @Test
    void atualizar_reativaItem() {
        OrdemServico ordem = new OrdemServico();
        ordem.setId(3L);

        Servico servico = new Servico(1L, "X", BigDecimal.ONE);
        ItemServico item = new ItemServico(ordem, servico, 1, BigDecimal.ONE);
        item.setAtivo(false);

        when(itemRepo.findById(40L)).thenReturn(Optional.of(item));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(3L)).thenReturn(List.of(item));

        var dto = new ItemServicoUpdateDTO(null, null, null, true);
        var resposta = service.atualizar(40L, dto);

        assertTrue(resposta.ativo());
    }

    @Test
    void deletar_itemNaoExiste() {
        when(itemRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deletar(99L));
    }

    @Test
    void deletar_sucesso() {
        OrdemServico ordem = new OrdemServico();
        ordem.setId(4L);

        Servico servico = new Servico(1L, "X", BigDecimal.valueOf(100));
        ItemServico item = new ItemServico(ordem, servico, 1, BigDecimal.valueOf(100));

        when(itemRepo.findById(50L)).thenReturn(Optional.of(item));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(4L)).thenReturn(List.of());

        service.deletar(50L);

        assertFalse(item.isAtivo());
        verify(itemRepo).save(item);
        verify(ordemRepo).save(ordem);
    }
}
