package br.com.ralfdomingues.oficina.controller.ordemservico;

import br.com.ralfdomingues.oficina.config.security.JwtFilter;
import br.com.ralfdomingues.oficina.config.security.JwtService;
import br.com.ralfdomingues.oficina.controller.itemservico.ItemServicoController;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoCreateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoResponseDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.dto.OrdemServicoUpdateDTO;
import br.com.ralfdomingues.oficina.domain.ordemservico.enums.StatusOrdemServico;
import br.com.ralfdomingues.oficina.domain.ordemservico.service.OrdemServicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@WebMvcTest(
        controllers = OrdemServicoController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
class OrdemServicoControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrdemServicoService service;

    @Test
    void deveCriarOS() throws Exception {

        OrdemServicoCreateDTO createDTO = new OrdemServicoCreateDTO(
                1L,
                10L,
                "Carro falhando",
                BigDecimal.valueOf(500),
                StatusOrdemServico.ABERTA
        );

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                100L,
                1L,
                10L,
                StatusOrdemServico.ABERTA,
                "Carro falhando",
                null,
                BigDecimal.valueOf(500),
                LocalDateTime.now(),
                null
        );

        Mockito.when(service.criar(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.descricao").value("Carro falhando"));
    }

    @Test
    void deveBuscarPorId() throws Exception {

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                20L,
                2L,
                30L,
                StatusOrdemServico.ABERTA,
                "Problema na suspensão",
                null,
                BigDecimal.valueOf(300),
                LocalDateTime.now(),
                null
        );

        Mockito.when(service.buscarPorId(20L)).thenReturn(responseDTO);

        mockMvc.perform(get("/ordens-servico/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.descricao").value("Problema na suspensão"));
    }

    @Test
    void deveListarOS() throws Exception {

        OrdemServicoResponseDTO os1 = new OrdemServicoResponseDTO(
                1L,
                1L,
                10L,
                StatusOrdemServico.ABERTA,
                "Troca de óleo",
                null,
                BigDecimal.valueOf(100),
                LocalDateTime.now(),
                null
        );

        OrdemServicoResponseDTO os2 = new OrdemServicoResponseDTO(
                2L,
                2L,
                20L,
                StatusOrdemServico.EM_ANDAMENTO,
                "Freio ruim",
                null,
                BigDecimal.valueOf(200),
                LocalDateTime.now(),
                null
        );

        Page<OrdemServicoResponseDTO> page =
                new PageImpl<>(List.of(os1, os2));

        Mockito.when(service.listar(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/ordens-servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].descricao").value("Troca de óleo"))
                .andExpect(jsonPath("$.content[1].descricao").value("Freio ruim"));
    }

    @Test
    void deveAtualizarOS() throws Exception {

        OrdemServicoUpdateDTO updateDTO = new OrdemServicoUpdateDTO(
                "Descrição atualizada",
                BigDecimal.valueOf(750),
                StatusOrdemServico.EM_ANDAMENTO
        );

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                99L,
                1L,
                10L,
                StatusOrdemServico.EM_ANDAMENTO,
                "Descrição atualizada",
                BigDecimal.valueOf(750),
                BigDecimal.valueOf(500),
                LocalDateTime.now(),
                null
        );

        Mockito.when(service.atualizar(eq(99L), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/ordens-servico/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"))
                .andExpect(jsonPath("$.valorFinal").value(750));
    }

    @Test
    void deveCancelarOS() throws Exception {

        Mockito.doNothing().when(service).deletar(55L);

        mockMvc.perform(delete("/ordens-servico/55"))
                .andExpect(status().isNoContent());
    }
}
