package br.com.ralfdomingues.oficina.controller.itemservico;

import br.com.ralfdomingues.oficina.config.security.JwtFilter;
import br.com.ralfdomingues.oficina.config.security.JwtService;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.dto.ItemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.itemservico.service.ItemServicoService;
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
        controllers = ItemServicoController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
class ItemServicoControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ItemServicoService service;

    @Test
    void deveCriarItemServico() throws Exception {

        ItemServicoCreateDTO createDTO = new ItemServicoCreateDTO(
                10L,
                5L,
                2
        );

        ItemServicoResponseDTO responseDTO = new ItemServicoResponseDTO(
                1L,
                10L,
                5L,
                "Troca de óleo",
                new BigDecimal("79.90"),
                2,
                new BigDecimal("159.80"),
                true
        );

        Mockito.when(service.criar(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/itens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordemServicoId").value(10L))
                .andExpect(jsonPath("$.servicoId").value(5L))
                .andExpect(jsonPath("$.quantidade").value(2));
    }

    @Test
    void deveListarTodos() throws Exception {

        Page<ItemServicoResponseDTO> page = new PageImpl<>(
                List.of(
                        new ItemServicoResponseDTO(
                                1L,
                                99L,
                                8L,
                                "Alinhamento",
                                new BigDecimal("120.00"),
                                1,
                                new BigDecimal("120.00"),
                                true
                        )
                )
        );

        Mockito.when(service.listarTodos(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/itens-servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nomeServico").value("Alinhamento"))
                .andExpect(jsonPath("$.content[0].valorUnitario").value(120.00));
    }


    @Test
    void deveBuscarPorId() throws Exception {

        Mockito.when(service.buscarPorId(7L))
                .thenReturn(
                        new ItemServicoResponseDTO(
                                7L,
                                50L,
                                3L,
                                "Balanceamento",
                                new BigDecimal("50.00"),
                                1,
                                new BigDecimal("50.00"),
                                true
                        )
                );

        mockMvc.perform(get("/itens-servico/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.nomeServico").value("Balanceamento"));
    }

    @Test
    void deveListarPorOrdem() throws Exception {

        Page<ItemServicoResponseDTO> page = new PageImpl<>(
                List.of(
                        new ItemServicoResponseDTO(
                                1L,
                                99L,
                                10L,
                                "Troca de filtro",
                                new BigDecimal("30.00"),
                                1,
                                new BigDecimal("30.00"),
                                true
                        ),
                        new ItemServicoResponseDTO(
                                2L,
                                99L,
                                11L,
                                "Revisão geral",
                                new BigDecimal("500.00"),
                                1,
                                new BigDecimal("500.00"),
                                true
                        )
                )
        );

        Mockito.when(service.listarPorOrdem(eq(99L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/itens-servico/ordem/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ordemServicoId").value(99L))
                .andExpect(jsonPath("$.content[1].nomeServico").value("Revisão geral"));
    }


    @Test
    void deveAtualizarItemServico() throws Exception {

        ItemServicoUpdateDTO updateDTO = new ItemServicoUpdateDTO(
                3L,
                5,
                120.0,
                true
        );

        ItemServicoResponseDTO responseDTO = new ItemServicoResponseDTO(
                12L,
                40L,
                3L,
                "Serviço atualizado",
                new BigDecimal("120.00"),
                5,
                new BigDecimal("600.00"),
                true
        );

        Mockito.when(service.atualizar(eq(12L), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/itens-servico/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12L))
                .andExpect(jsonPath("$.quantidade").value(5));
    }

    @Test
    void deveDeletarItemServico() throws Exception {

        Mockito.doNothing().when(service).deletar(3L);

        mockMvc.perform(delete("/itens-servico/3"))
                .andExpect(status().isNoContent());
    }
}
