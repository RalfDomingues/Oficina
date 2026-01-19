package br.com.ralfdomingues.oficina.controller.auth;

import br.com.ralfdomingues.oficina.domain.auth.dto.LoginRequestDTO;
import br.com.ralfdomingues.oficina.domain.auth.dto.LoginResponseDTO;
import br.com.ralfdomingues.oficina.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo fluxo de autenticação da aplicação.
 *
 * <p>
 * Atua exclusivamente como camada de entrada HTTP, delegando toda a
 * lógica de autenticação, validação de credenciais e geração de tokens
 * ao {@link AuthService}.
 *
 * <p>
 * Esta separação mantém o controller intencionalmente simples,
 * facilitando testes, manutenção e evolução das regras de autenticação.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /**
     * Realiza a autenticação do usuário e retorna o token JWT.
     *
     * <p>
     * O endpoint é público e não exige autenticação prévia.
     *
     * @param dto credenciais de login validadas
     * @return token JWT e informações básicas do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto) {

        return ResponseEntity.ok(service.login(dto));
    }

    /**
     * Endpoint simbólico de logout.
     *
     * <p>
     * Em uma arquitetura stateless baseada em JWT, o logout não envolve
     * invalidação de sessão no servidor. Este endpoint existe para
     * padronização da API e integração com o frontend.
     *
     * @return resposta sem conteúdo (204)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

}
