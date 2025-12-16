package br.com.ralfdomingues.oficina.domain.ordemservico.service;

import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.entity.OrdemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import br.com.ralfdomingues.oficina.domain.veiculo.entity.Veiculo;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import br.com.ralfdomingues.oficina.repository.itemservico.ItemServicoRepository;
import br.com.ralfdomingues.oficina.repository.ordemservico.OrdemServicoRepository;
import br.com.ralfdomingues.oficina.repository.veiculo.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class OrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepository ordemRepo;
    @Mock
    private ClienteRepository clienteRepo;
    @Mock
    private VeiculoRepository veiculoRepo;
    @Mock
    private ItemServicoRepository itemRepo;

    @InjectMocks
    private OrdemServicoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criar_clienteNaoEncontrado_deveLancarNotFound() {
        when(clienteRepo.findById(1L)).thenReturn(Optional.empty());
        var dto = new OrdemServicoCreateDTO(1L, 2L, "desc", BigDecimal.valueOf(100), null);
        assertThrows(NotFoundException.class, () -> service.criar(dto));
    }

    @Test
    void criar_veiculoNaoEncontrado_deveLancarNotFound() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);
        when(clienteRepo.findById(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findById(2L)).thenReturn(Optional.empty());
        var dto = new OrdemServicoCreateDTO(1L, 2L, "desc", BigDecimal.valueOf(100), null);
        assertThrows(NotFoundException.class, () -> service.criar(dto));
    }

    @Test
    void criar_clienteInativo_deveLancarBusiness() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(false);
        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setCliente(cliente);
        when(clienteRepo.findById(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findById(2L)).thenReturn(Optional.of(veiculo));
        var dto = new OrdemServicoCreateDTO(1L, 2L, "desc", BigDecimal.valueOf(100), null);
        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void criar_veiculoNaoPertenceAoCliente_deveLancarBusiness() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);
        Cliente outro = new Cliente();
        outro.setId(99L);
        outro.setAtivo(true);
        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setCliente(outro);
        when(clienteRepo.findById(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findById(2L)).thenReturn(Optional.of(veiculo));
        var dto = new OrdemServicoCreateDTO(1L, 2L, "desc", BigDecimal.valueOf(100), null);
        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void criar_sucesso_retornaDTOComStatusAberta() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);
        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setCliente(cliente);
        when(clienteRepo.findById(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findById(2L)).thenReturn(Optional.of(veiculo));
        var dto = new OrdemServicoCreateDTO(1L, 2L, "desc", BigDecimal.valueOf(100), null);
        var resposta = service.criar(dto);
        assertNotNull(resposta);
        assertEquals(StatusOrdemServico.ABERTA, resposta.status());
    }

    @Test
    void atualizar_concluirSemItens_deveLancarBusiness() {
        OrdemServico os = new OrdemServico();
        os.setId(5L);
        os.setStatus(StatusOrdemServico.ABERTA);
        when(ordemRepo.findById(5L)).thenReturn(Optional.of(os));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(5L)).thenReturn(Collections.emptyList());
        var dto = new OrdemServicoUpdateDTO(null, null, StatusOrdemServico.CONCLUIDA);
        assertThrows(BusinessException.class, () -> service.atualizar(5L, dto));
    }

    @Test
    void atualizar_concluirSemValorFinal_deveLancarBusiness() {
        OrdemServico os = new OrdemServico();
        os.setId(6L);
        os.setStatus(StatusOrdemServico.ABERTA);
        when(ordemRepo.findById(6L)).thenReturn(Optional.of(os));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(6L)).thenReturn(Collections.singletonList(new br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico()));
        var dto = new OrdemServicoUpdateDTO(null, null, StatusOrdemServico.CONCLUIDA);
        assertThrows(BusinessException.class, () -> service.atualizar(6L, dto));
    }

    @Test
    void atualizar_concluirComValor_finalSetadoEDataConclusaoRegistrada() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setCliente(cliente);

        OrdemServico os = new OrdemServico();
        os.setId(7L);
        os.setStatus(StatusOrdemServico.ABERTA);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);

        when(ordemRepo.findById(7L)).thenReturn(Optional.of(os));
        when(itemRepo.findAllByOrdem_IdAndAtivoTrue(7L))
                .thenReturn(Collections.singletonList(new br.com.ralfdomingues.oficina.domain.itemservico.entity.ItemServico()));

        var dto = new OrdemServicoUpdateDTO(null, BigDecimal.valueOf(100), StatusOrdemServico.CONCLUIDA);

        var resposta = service.atualizar(7L, dto);

        assertNotNull(resposta.dataConclusao());
        assertEquals(StatusOrdemServico.CONCLUIDA, resposta.status());
    }

    @Test
    void buscarPorId_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setCliente(cliente);
        veiculo.setPlaca("ABC1A11");

        OrdemServico os = new OrdemServico();
        os.setId(10L);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setStatus(StatusOrdemServico.ABERTA);

        when(ordemRepo.findByIdAndStatusNot(10L, StatusOrdemServico.CANCELADA))
                .thenReturn(Optional.of(os));


        var resposta = service.buscarPorId(10L);

        assertNotNull(resposta);
        assertEquals(10L, resposta.id());
    }


    @Test
    void deletar_concluida_deveLancarBusiness() {
        OrdemServico os = new OrdemServico();
        os.setId(8L);
        os.setStatus(StatusOrdemServico.CONCLUIDA);
        when(ordemRepo.findById(8L)).thenReturn(Optional.of(os));
        assertThrows(BusinessException.class, () -> service.deletar(8L));
    }

    @Test
    void buscarPorId_naoEncontrado_deveLancarNotFound() {
        when(ordemRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listar_sucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setCliente(cliente);

        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);

        Pageable pageable = PageRequest.of(0, 10);
        Page<OrdemServico> page = new PageImpl<>(List.of(os));

        when(ordemRepo.findAllByStatusNot(StatusOrdemServico.CANCELADA, pageable))
                .thenReturn(page);

        var resultado = service.listar(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L, resultado.getContent().get(0).id());
    }



    @Test
    void listar_vazio() {
        Pageable pageable = PageRequest.of(0, 10);

        when(ordemRepo.findAllByStatusNot(StatusOrdemServico.CANCELADA, pageable))
                .thenReturn(Page.empty());

        var resultado = service.listar(pageable);

        assertTrue(resultado.isEmpty());
    }



    @Test
    void deletar_sucesso_setaCanceladaEDataConclusao() {
        OrdemServico os = new OrdemServico();
        os.setId(9L);
        os.setStatus(StatusOrdemServico.ABERTA);
        when(ordemRepo.findById(9L)).thenReturn(Optional.of(os));
        service.deletar(9L);
        assertEquals(StatusOrdemServico.CANCELADA, os.getStatus());
        assertNotNull(os.getDataConclusao());
        verify(ordemRepo).save(os);
    }
}
