package br.com.ralfdomingues.oficina.infra.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger =
            LoggerFactory.getLogger("API_LOGGER");

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String usuario = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : "ANONIMO";

        logger.info(
                "USUARIO={} | METODO={} | URI={} | DATA={}",
                usuario,
                request.getMethod(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return true;
    }
}
