package br.com.ralfdomingues.oficina.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Configuração central de segurança da aplicação.
 *
 * <p>
 * Define a política de autenticação e autorização baseada em JWT
 * (abordagem stateless), controlando o acesso aos endpoints conforme
 * os perfis de usuário do domínio da oficina.
 *
 * <p>
 * Esta classe centraliza todas as regras de acesso por path e método HTTP,
 * evitando anotações dispersas nos controllers e facilitando manutenção,
 * auditoria e entendimento do sistema.
 *
 * <p>
 * Perfis existentes no sistema:
 * <ul>
 *   <li><b>ADMIN</b>: acesso total e funções administrativas</li>
 *   <li><b>SECRETARIA</b>: operações administrativas e acompanhamento</li>
 *   <li><b>MECANICO</b>: execução técnica e atualização de ordens</li>
 * </ul>
 *
 * <p>
 * A separação de permissões reflete responsabilidades reais do negócio,
 * reduzindo riscos de acesso indevido e mantendo coerência operacional.
 */
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(
            JwtFilter jwtFilter,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtFilter = jwtFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Expõe o {@link AuthenticationManager} utilizado no fluxo de autenticação,
     * delegando sua criação à configuração padrão do Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define a cadeia de filtros e as regras de segurança da aplicação.
     *
     * <p>
     * Decisões arquiteturais adotadas:
     * <ul>
     *   <li>CSRF desabilitado por se tratar de uma API stateless</li>
     *   <li>Ausência de sessão HTTP (JWT)</li>
     *   <li>Tratamento centralizado de erros 401 e 403</li>
     *   <li>Controle de acesso por path e método HTTP</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth

                        // Endpoints públicos
                        .requestMatchers("/auth/**").permitAll()

                        // Gestão de usuários (restrito à administração)
                        .requestMatchers("/usuarios/**").hasAuthority("ADMIN")

                        // Dashboard administrativo
                        .requestMatchers("/dashboard/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        // Cadastros administrativos
                        .requestMatchers("/clientes/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")
                        .requestMatchers("/veiculos/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        // Serviços: leitura ampla, escrita restrita
                        .requestMatchers(HttpMethod.POST, "/servicos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/servicos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/servicos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/servicos/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA", "MECANICO")

                        // Itens de serviço
                        .requestMatchers(HttpMethod.GET, "/itens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA", "MECANICO")
                        .requestMatchers("/itens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        // Ordens de serviço
                        .requestMatchers(HttpMethod.GET, "/ordens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA", "MECANICO")
                        .requestMatchers(HttpMethod.PUT, "/ordens-servico/concluir/**")
                        .hasAnyAuthority("ADMIN", "MECANICO")
                        .requestMatchers("/ordens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        .anyRequest().authenticated()
                )
                // Filtro JWT executado antes do fluxo padrão de autenticação
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
