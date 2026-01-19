package br.com.ralfdomingues.oficina.domain.cliente.service;

import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteCreateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteResponseDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteUpdateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.entity.Cliente;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.cliente.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    @Test
    void listar_retornaApenasAtivos() {

        Cliente cliente = new Cliente();
        cliente.setId(4L);
        cliente.setNome("X");
        cliente.setAtivo(true);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> page = new PageImpl<>(List.of(cliente));

        when(repository.findAllByAtivoTrue(pageable)).thenReturn(page);

        Page<ClienteResponseDTO> resultado = service.listar(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals("X", resultado.getContent().get(0).nome());
    }

}
