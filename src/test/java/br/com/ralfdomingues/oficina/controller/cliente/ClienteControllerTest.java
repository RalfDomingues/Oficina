package br.com.ralfdomingues.oficina.controller.cliente;

import br.com.ralfdomingues.oficina.config.security.JwtFilter;
import br.com.ralfdomingues.oficina.config.security.JwtService;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteCreateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteResponseDTO;
import br.com.ralfdomingues.oficina.domain.cliente.dto.ClienteUpdateDTO;
import br.com.ralfdomingues.oficina.domain.cliente.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ClienteController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ClienteService service;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void deveCriarCliente() throws Exception {
        ClienteCreateDTO createDTO = new ClienteCreateDTO(
                "Carlos",
                "11111111111",
                "11999999999",
                "Rua X"
        );

        ClienteResponseDTO responseDTO = new ClienteResponseDTO(
                1L,
                "Carlos",
                "11111111111",
                "11999999999",
                "Rua X",
                true
        );

        Mockito.when(service.criar(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deveListarClientes() throws Exception {

        Page<ClienteResponseDTO> page = new PageImpl<>(
                List.of(
                        new ClienteResponseDTO(
                                1L,
                                "Maria",
                                "22222222222",
                                "11888888888",
                                "Rua Y",
                                true
                        )
                )
        );

        Mockito.when(service.listar(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Maria"));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Mockito.when(service.buscarPorId(5L))
                .thenReturn(
                        new ClienteResponseDTO(
                                5L,
                                "João",
                                "33333333333",
                                "11777777777",
                                "Rua Z",
                                true
                        )
                );

        mockMvc.perform(get("/clientes/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        ClienteUpdateDTO updateDTO = new ClienteUpdateDTO(
                "José Atualizado",
                "44444444444",
                "11666666666",
                true
        );

        ClienteResponseDTO responseDTO = new ClienteResponseDTO(
                10L,
                "José Atualizado",
                "44444444444",
                "11666666666",
                "Rua Nova",
                true
        );

        Mockito.when(service.atualizar(eq(10L), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/clientes/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("José Atualizado"));
    }

    @Test
    void deveDeletarCliente() throws Exception {
        Mockito.doNothing().when(service).deletar(3L);

        mockMvc.perform(delete("/clientes/3"))
                .andExpect(status().isNoContent());
    }
}
