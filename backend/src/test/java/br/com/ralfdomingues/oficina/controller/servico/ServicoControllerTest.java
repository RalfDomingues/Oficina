package br.com.ralfdomingues.oficina.controller.servico;

import br.com.ralfdomingues.oficina.config.security.JwtFilter;
import br.com.ralfdomingues.oficina.config.security.JwtService;
import br.com.ralfdomingues.oficina.domain.servico.dto.ServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.servico.dto.ServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.servico.dto.ServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.servico.service.ServicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ServicoController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ServicoService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveCriarServico() throws Exception {

        ServicoCreateDTO createDTO = new ServicoCreateDTO(
                "Balanceamento",
                BigDecimal.valueOf(80.0)
        );

        ServicoResponseDTO responseDTO = new ServicoResponseDTO(
                1L,
                "Balanceamento",
                BigDecimal.valueOf(80.0),
                true
        );

        Mockito.when(service.criar(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Balanceamento"))
                .andExpect(jsonPath("$.preco").value(80.0));
    }

    @Test
    void deveListarServicos() throws Exception {

        Page<ServicoResponseDTO> page = new PageImpl<>(
                List.of(
                        new ServicoResponseDTO(1L, "Troca de óleo", BigDecimal.valueOf(120), true),
                        new ServicoResponseDTO(2L, "Alinhamento", BigDecimal.valueOf(150), true)
                )
        );

        Mockito.when(service.listar(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Troca de óleo"))
                .andExpect(jsonPath("$.content[1].nome").value("Alinhamento"));
    }

    @Test
    void deveBuscarServicoPorId() throws Exception {

        ServicoResponseDTO responseDTO = new ServicoResponseDTO(
                10L,
                "Lavagem Completa",
                BigDecimal.valueOf(50),
                true
        );

        Mockito.when(service.listarPorId(10L)).thenReturn(responseDTO);

        mockMvc.perform(get("/servicos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Lavagem Completa"));
    }

    @Test
    void deveAtualizarServico() throws Exception {

        ServicoUpdateDTO updateDTO = new ServicoUpdateDTO(
                "Polimento",
                BigDecimal.valueOf(300),
                true
        );

        ServicoResponseDTO responseDTO = new ServicoResponseDTO(
                5L,
                "Polimento",
                BigDecimal.valueOf(300),
                true
        );

        Mockito.when(service.atualizar(eq(5L), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/servicos/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.nome").value("Polimento"))
                .andExpect(jsonPath("$.preco").value(300));
    }

    @Test
    void deveDeletarServico() throws Exception {

        Mockito.doNothing().when(service).deletar(7L);

        mockMvc.perform(delete("/servicos/7"))
                .andExpect(status().isNoContent());
    }
}
