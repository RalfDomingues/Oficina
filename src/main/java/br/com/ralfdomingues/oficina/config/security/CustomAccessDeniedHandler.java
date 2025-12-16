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

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger =
            LoggerFactory.getLogger("API_LOGGER");

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        String usuario = Optional.ofNullable(
                        SecurityContextHolder.getContext().getAuthentication()
                )
                .map(auth -> auth.getName())
                .orElse("ANONIMO");

        logger.warn(
                "ACESSO_NEGADO | USUARIO={} | METODO={} | URI={}",
                usuario,
                request.getMethod(),
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Acesso negado");
    }
}
