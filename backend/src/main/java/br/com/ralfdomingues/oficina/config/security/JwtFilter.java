package br.com.ralfdomingues.oficina.config.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro responsável por validar tokens JWT e popular o contexto de segurança
 * da aplicação para cada requisição HTTP.
 *
 * <p>
 * Este filtro é executado uma única vez por requisição e atua antes do
 * processamento dos controllers, garantindo que o {@link SecurityContextHolder}
 * esteja corretamente preenchido quando necessário.
 *
 * <p>
 * Estratégia adotada:
 * <ul>
 *   <li>Extrai o token JWT do header Authorization (Bearer)</li>
 *   <li>Recupera o email do usuário a partir do token</li>
 *   <li>Carrega os dados do usuário via {@link CustomUserDetailsService}</li>
 *   <li>Define a autenticação no contexto de segurança</li>
 * </ul>
 *
 * <p>
 * Observações importantes:
 * <ul>
 *   <li>Não bloqueia a requisição caso o token seja inválido ou inexistente</li>
 *   <li>Falhas de autenticação são tratadas posteriormente pelo Spring Security</li>
 *   <li>Não mantém estado de sessão (abordagem stateless)</li>
 * </ul>
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService,
                     CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Processa a requisição HTTP validando o token JWT, quando presente,
     * e associando o usuário autenticado ao contexto de segurança.
     *
     * @param request     requisição HTTP
     * @param response    resposta HTTP
     * @param filterChain cadeia de filtros
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            String email = jwtService.getEmail(token);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
