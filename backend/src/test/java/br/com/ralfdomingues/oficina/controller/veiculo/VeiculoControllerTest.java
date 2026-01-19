package br.com.ralfdomingues.oficina.controller.veiculo;

import br.com.ralfdomingues.oficina.config.security.JwtFilter;
import br.com.ralfdomingues.oficina.config.security.JwtService;
import br.com.ralfdomingues.oficina.domain.veiculo.dto.*;
import br.com.ralfdomingues.oficina.domain.veiculo.enums.TipoVeiculo;
import br.com.ralfdomingues.oficina.domain.veiculo.service.VeiculoService;
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
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = VeiculoController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    private VeiculoService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveCriarVeiculo() throws Exception {

        VeiculoCreateDTO dto = new VeiculoCreateDTO(
                "ABC1D23",
                "Civic",
                "Honda",
                2020,
                TipoVeiculo.CARRO,
                10L
        );

        VeiculoResponseDTO response = new VeiculoResponseDTO(
                1L,
                "ABC1D23",
                "Civic",
                "Honda",
                2020,
                TipoVeiculo.CARRO,
                10L,
                true
        );

        Mockito.when(service.criar(any())).thenReturn(response);

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.modelo").value("Civic"));
    }

    @Test
    void deveListarVeiculos() throws Exception {

        Page<VeiculoResponseDTO> page = new PageImpl<>(
                List.of(
                        new VeiculoResponseDTO(
                                1L, "PLK9A87", "Gol", "VW",
                                2010, TipoVeiculo.CARRO, 5L, true
                        )
                )
        );

        Mockito.when(service.listar(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].placa").value("PLK9A87"));
    }

    @Test
    void deveListarPorCliente() throws Exception {

        Page<VeiculoResponseDTO> page = new PageImpl<>(
                List.of(
                        new VeiculoResponseDTO(
                                2L, "ZZZ1X22", "Onix", "Chevrolet",
                                2022, TipoVeiculo.CARRO, 99L, true
                        )
                )
        );

        Mockito.when(service.listarPorCliente(eq(99L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/veiculos/cliente/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].clienteId").value(99L));
    }

    @Test
    void deveBuscarPorId() throws Exception {

        Mockito.when(service.buscarPorId(3L)).thenReturn(
                new VeiculoResponseDTO(
                        3L, "GGH2D55", "Corolla", "Toyota",
                        2018, TipoVeiculo.CARRO, 20L, true
                )
        );

        mockMvc.perform(get("/veiculos/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Corolla"));
    }

    @Test
    void deveAtualizarVeiculo() throws Exception {

        VeiculoUpdateDTO dto = new VeiculoUpdateDTO(
                "HR-V",
                "Honda",
                2023,
                TipoVeiculo.CARRO,
                true
        );

        VeiculoResponseDTO updated = new VeiculoResponseDTO(
                7L, "AAA1B11", "HR-V", "Honda",
                2023, TipoVeiculo.CARRO, 88L, true
        );

        Mockito.when(service.atualizar(eq(7L), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/veiculos/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("HR-V"))
                .andExpect(jsonPath("$.ano").value(2023));
    }

    @Test
    void deveDeletarVeiculo() throws Exception {

        Mockito.doNothing().when(service).deletar(4L);

        mockMvc.perform(delete("/veiculos/4"))
                .andExpect(status().isNoContent());
    }
}
