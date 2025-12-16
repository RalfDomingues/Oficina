package br.com.ralfdomingues.oficina.domain.auth.service;


import br.com.ralfdomingues.oficina.config.security.JwtService;
import br.com.ralfdomingues.oficina.domain.auth.dto.LoginRequestDTO;
import br.com.ralfdomingues.oficina.domain.auth.dto.LoginResponseDTO;
import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import br.com.ralfdomingues.oficina.repository.usuario.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger("API_LOGGER");

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            HttpServletRequest request
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.request = request;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.senha()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(dto.email())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            log.info(
                    "LOGIN_SUCESSO | USUARIO={} | PERFIL={} | IP={}",
                    usuario.getEmail(),
                    usuario.getPerfil(),
                    request.getRemoteAddr()
            );

            String token = jwtService.gerarToken(usuario);

            return new LoginResponseDTO(
                    token,
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getPerfil()
            );

        } catch (BadCredentialsException ex) {

            log.warn(
                    "LOGIN_FALHA | USUARIO={} | MOTIVO=senha_invalida | IP={}",
                    dto.email(),
                    request.getRemoteAddr()
            );
            throw ex;

        } catch (AuthenticationException ex) {

            log.warn(
                    "LOGIN_FALHA | USUARIO={} | MOTIVO=erro_autenticacao | IP={}",
                    dto.email(),
                    request.getRemoteAddr()
            );
            throw ex;
        }
    }
}

