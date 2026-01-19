package br.com.ralfdomingues.oficina.domain.servico.service;

import br.com.ralfdomingues.oficina.domain.servico.dto.ServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.servico.dto.ServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.servico.dto.ServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.servico.entity.Servico;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.servico.ServicoRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private ServicoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPorId_naoEncontrado_deveLancar() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.listarPorId(1L));
    }

    @Test
    void listarPorId_sucesso() {
        Servico servico = new Servico(1L, "Troca de óleo", BigDecimal.TEN);
        when(repository.findById(1L)).thenReturn(Optional.of(servico));

        var resposta = service.listarPorId(1L);

        assertNotNull(resposta);
        assertEquals(1L, resposta.id());
        assertEquals("Troca de óleo", resposta.nome());
        assertEquals(BigDecimal.TEN, resposta.preco());
    }

    @Test
    void criar_sucesso() {
        ServicoCreateDTO dto = new ServicoCreateDTO("Alinhamento", BigDecimal.valueOf(50));

        when(repository.save(any(Servico.class))).thenAnswer(invocation -> {
            Servico s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });

        var resposta = service.criar(dto);

        assertNotNull(resposta);
        assertEquals(10L, resposta.id());
        assertEquals("Alinhamento", resposta.nome());
        assertEquals(BigDecimal.valueOf(50), resposta.preco());
    }


    @Test
    void atualizar_naoEncontrado_deveLancar() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        var dto = new ServicoUpdateDTO("Novo nome", BigDecimal.ONE, true);
        assertThrows(NotFoundException.class, () -> service.atualizar(2L, dto));
    }

    @Test
    void atualizar_sucesso() {
        Servico servico = new Servico(3L, "Antigo", BigDecimal.TEN);
        when(repository.findById(3L)).thenReturn(Optional.of(servico));

        var dto = new ServicoUpdateDTO("Novo", BigDecimal.valueOf(99), false);

        var resposta = service.atualizar(3L, dto);

        assertEquals("Novo", resposta.nome());
        assertEquals(BigDecimal.valueOf(99), resposta.preco());
        assertFalse(resposta.ativo());
    }

    @Test
    void listarTodos_sucesso() {

        Servico servico = new Servico(1L, "X", BigDecimal.ONE);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Servico> page = new PageImpl<>(List.of(servico));

        when(repository.findAllByAtivoTrue(pageable)).thenReturn(page);

        Page<ServicoResponseDTO> resultado = service.listar(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L, resultado.getContent().get(0).id());
    }


    @Test
    void deletar_deveSetarAtivoFalse() {
        Servico servico = new Servico(5L, "a", BigDecimal.TEN);
        when(repository.findById(5L)).thenReturn(Optional.of(servico));

        service.deletar(5L);

        assertFalse(servico.getAtivo());
        verify(repository).save(servico);
    }

    @Test
    void deletar_naoEncontrado_deveLancar() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.deletar(99L));
    }
}
