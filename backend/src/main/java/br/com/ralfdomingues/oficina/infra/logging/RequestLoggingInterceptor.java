package br.com.ralfdomingues.oficina.infra.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.LocalDateTime;

/**
 * Interceptor responsável pelo registro de requisições HTTP.
 *
 * <p>
 * Atua antes da execução dos controllers, registrando informações
 * básicas para auditoria e rastreabilidade das operações da API.
 *
 * <p>
 * Não interfere no fluxo da requisição nem altera o comportamento
 * da aplicação.
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger =
            LoggerFactory.getLogger("API_LOGGER");

    /**
     * Registra informações da requisição antes do processamento.
     *
     * <p>
     * Caso o usuário não esteja autenticado, o log registra
     * o valor {@code ANONIMO}.
     */
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
