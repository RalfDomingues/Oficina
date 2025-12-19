package br.com.ralfdomingues.oficina.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Entry point customizado para requisições que acessam recursos protegidos
 * sem que haja um usuário autenticado (HTTP 401).
 *
 * <p>
 * Diferente do {@link CustomAccessDeniedHandler}, esta classe trata
 * exclusivamente cenários onde não existe autenticação válida no contexto
 * de segurança (ex: ausência ou token JWT inválido).
 *
 * <p>
 * Centraliza:
 * <ul>
 *   <li>O registro em log de tentativas de acesso não autenticadas</li>
 *   <li>A padronização da resposta HTTP para falhas de autenticação</li>
 * </ul>
 */
@Component
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    /**
     * Logger dedicado à auditoria de eventos de segurança da API.
     */
    private static final Logger logger =
            LoggerFactory.getLogger("API_LOGGER");

    /**
     * Executado pelo Spring Security quando uma requisição protegida
     * é realizada sem autenticação válida.
     *
     * @param request  requisição HTTP original
     * @param response resposta HTTP a ser retornada ao cliente
     * @param ex       exceção de autenticação lançada pelo framework
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {

        // Registra tentativa de acesso sem autenticação
        logger.warn(
                "NAO_AUTENTICADO | METODO={} | URI={}",
                request.getMethod(),
                request.getRequestURI()
        );

        // Retorna resposta padronizada para falha de autenticação
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Não autenticado");
    }
}
