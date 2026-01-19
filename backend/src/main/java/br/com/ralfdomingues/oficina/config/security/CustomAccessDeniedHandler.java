package br.com.ralfdomingues.oficina.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

/**
 * Handler customizado para tratar tentativas de acesso a recursos
 * protegidos quando o usuário está autenticado, porém não possui
 * autorização suficiente (HTTP 403).
 *
 * <p>
 * Responsabilidades principais:
 * <ul>
 *   <li>Registrar em log tentativas de acesso negado para fins de auditoria</li>
 *   <li>Identificar o usuário autenticado no momento da requisição</li>
 *   <li>Padronizar a resposta HTTP para cenários de autorização insuficiente</li>
 * </ul>
 *
 * <p>
 * A separação dessa lógica em um handler dedicado evita tratamento
 * disperso nos controllers e centraliza o comportamento de segurança.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Logger dedicado à auditoria de eventos da API.
     * Utiliza um nome fixo para facilitar filtros e análise de logs.
     */
    private static final Logger logger =
            LoggerFactory.getLogger("API_LOGGER");

    /**
     * Executado automaticamente pelo Spring Security quando um usuário
     * autenticado tenta acessar um recurso para o qual não possui permissão.
     *
     * @param request  requisição HTTP original
     * @param response resposta HTTP a ser retornada ao cliente
     * @param ex       exceção lançada pelo mecanismo de autorização
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        // Recupera o usuário autenticado, se existir.
        // Em cenários extremos (ex: contexto inválido), assume ANONIMO.
        String usuario = Optional.ofNullable(
                        SecurityContextHolder.getContext().getAuthentication()
                )
                .map(auth -> auth.getName())
                .orElse("ANONIMO");

        // Loga tentativa de acesso negado com informações relevantes
        logger.warn(
                "ACESSO_NEGADO | USUARIO={} | METODO={} | URI={}",
                usuario,
                request.getMethod(),
                request.getRequestURI()
        );

        // Retorna resposta simples e direta ao cliente
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Acesso negado");
    }
}
