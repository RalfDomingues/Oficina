package br.com.ralfdomingues.oficina.domain.veiculo.service;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.veiculo.dto.*;
import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import br.com.ralfdomingues.oficina.repository.veiculo.VeiculoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VeiculoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void criar_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);

        VeiculoCreateDTO dto = new VeiculoCreateDTO(
                "ABC1234", "Gol", "VW", 2015, TipoVeiculo.CARRO, 1L
        );

        when(veiculoRepository.existsByPlaca("ABC1234")).thenReturn(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Veiculo salvado = Veiculo.builder()
                .id(10L)
                .placa("ABC1234")
                .modelo("Gol")
                .marca("VW")
                .ano(2015)
                .tipo(TipoVeiculo.CARRO)
                .cliente(cliente)
                .build();

        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(salvado);

        var resp = service.criar(dto);

        assertNotNull(resp);
        assertEquals(10L, resp.id());
        assertEquals("ABC1234", resp.placa());
    }


    @Test
    void listar_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo veiculo = Veiculo.builder()
                .id(5L)
                .placa("XYZ9999")
                .modelo("Civic")
                .marca("Honda")
                .ano(2019)
                .tipo(TipoVeiculo.CARRO)
                .cliente(cliente)
                .ativo(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> page = new PageImpl<>(List.of(veiculo));

        when(veiculoRepository.findAllByAtivoTrue(pageable))
                .thenReturn(page);

        var resultado = service.listar(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("XYZ9999", resultado.getContent().get(0).placa());
    }


    @Test
    void listarPorCliente_sucesso() {
        Cliente c = new Cliente();
        c.setId(1L);

        Veiculo veiculo = Veiculo.builder()
                .id(7L)
                .placa("AAA1234")
                .cliente(c)
                .ativo(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> page = new PageImpl<>(List.of(veiculo));

        when(veiculoRepository.findAllByCliente_IdAndAtivoTrue(1L, pageable))
                .thenReturn(page);

        var resultado = service.listarPorCliente(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(7L, resultado.getContent().get(0).id());
    }

    @Test
    void buscarPorId_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(99L);

        Veiculo veiculo = Veiculo.builder()
                .id(20L)
                .placa("QWE1111")
                .cliente(cliente)
                .ativo(true)
                .build();

        when(veiculoRepository.findById(20L)).thenReturn(Optional.of(veiculo));

        var resposta = service.buscarPorId(20L);

        assertEquals(20L, resposta.id());
    }


    @Test
    void atualizar_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo v = Veiculo.builder()
                .id(30L)
                .placa("OPA0001")
                .modelo("Onix")
                .marca("GM")
                .ano(2019)
                .cliente(cliente)
                .ativo(true)
                .build();

        when(veiculoRepository.findById(30L)).thenReturn(Optional.of(v));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(i -> i.getArgument(0));

        VeiculoUpdateDTO dto = new VeiculoUpdateDTO(
                "Onix Plus", null, null, null, null
        );

        var resposta = service.atualizar(30L, dto);

        assertEquals("Onix Plus", resposta.modelo());
    }


    @Test
    void deletar_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo v = Veiculo.builder()
                .id(40L)
                .placa("DEL0000")
                .cliente(cliente)
                .ativo(true)
                .build();

        when(veiculoRepository.findById(40L)).thenReturn(Optional.of(v));

        service.deletar(40L);

        assertFalse(v.getAtivo());
        verify(veiculoRepository).save(v);
    }

}
