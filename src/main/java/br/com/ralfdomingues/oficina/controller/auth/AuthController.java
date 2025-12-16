package br.com.ralfdomingues.oficina.controller.auth;

import br.com.ralfdomingues.oficina.domain.auth.dto.LoginRequestDTO;
import br.com.ralfdomingues.oficina.domain.auth.dto.LoginResponseDTO;
import br.com.ralfdomingues.oficina.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto) {

        return ResponseEntity.ok(service.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

}
