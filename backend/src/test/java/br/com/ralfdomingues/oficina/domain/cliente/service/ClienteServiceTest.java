package br.com.ralfdomingues.oficina.domain.cliente.service;

import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteCreateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteUpdateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock private ClienteRepository repository;
    @InjectMocks private ClienteService service;

    @BeforeEach void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void criar_cpfExistente_deveLancarBusiness() {
        when(repository.existsByCpf("123")).thenReturn(true);
        var dto = new ClienteCreateDTO("nome", "telefone", "123", "email");
        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void criar_sucesso_salvaERetornaDTO() {
        when(repository.existsByCpf("123")).thenReturn(false);
        var dto = new ClienteCreateDTO("nome", "telefone", "123", "email");
        var resp = service.criar(dto);
        assertEquals("nome", resp.nome());
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void buscarPorId_inexistente_deveLancarNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.buscarPorId(1L));
    }

    @Test
    void atualizar_naoEncontrado_deveLancarBusiness() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        var dto = new ClienteUpdateDTO("novo", "tel", "email", true);
        assertThrows(BusinessException.class, () -> service.atualizar(2L, dto));
    }

    @Test
    void deletar_sucesso_inativaCliente() {
        Cliente cliente = new Cliente(); cliente.setId(3L); cliente.setAtivo(true);
        when(repository.findById(3L)).thenReturn(Optional.of(cliente));
        service.deletar(3L);
        assertFalse(cliente.getAtivo());
        verify(repository).save(cliente);
    }

}
