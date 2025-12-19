package br.com.ralfdomingues.oficina.config;

import br.com.ralfdomingues.oficina.infra.logging.RequestLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração global de interceptadores HTTP da aplicação.
 *
 * <p>
 * Centraliza o registro de interceptadores do Spring MVC,
 * permitindo aplicar comportamentos transversais a todas
 * as requisições, como logging, auditoria ou métricas.
 *
 * <p>
 * O uso de um interceptor global evita duplicação de código
 * em controllers e garante padronização no tratamento das requisições.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor loggingInterceptor;

    public WebConfig(RequestLoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    /**
     * Registra interceptadores que serão executados antes e/ou depois
     * do processamento das requisições HTTP.
     *
     * <p>
     * O {@link RequestLoggingInterceptor} é aplicado a todos os endpoints
     * da API, garantindo rastreabilidade completa das chamadas.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**");
    }
}
