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
 * =========================================================
 * CONFIGURAÇÃO DE SEGURANÇA – CONTROLE DE ACESSO POR PERFIL
 * =========================================================
 * <p>
 * O sistema utiliza autenticação baseada em JWT (stateless),
 * onde o acesso às APIs é controlado por perfil de usuário,
 * refletindo as responsabilidades reais dentro de uma oficina mecânica.
 * <p>
 * Perfis existentes:
 * <p>
 * ADMIN
 * - Representa o dono ou diretor da oficina.
 * - Possui acesso total ao sistema, incluindo funções administrativas
 * e de gestão estratégica.
 * <p>
 * SECRETARIA
 * - Representa o papel administrativo operacional.
 * - Responsável por cadastros, organização e acompanhamento de serviços.
 * <p>
 * MECANICO
 * - Representa o papel técnico.
 * - Responsável pela execução e atualização das ordens de serviço.
 * <p>
 * ---------------------------------------------------------
 * MAPEAMENTO DE ACESSO POR PATH
 * ---------------------------------------------------------
 * <p>
 * /auth/**
 * - Público
 * - Utilizado para autenticação (login e logout).
 * - Não exige token JWT.
 * <p>
 * /usuarios/**
 * - ADMIN
 * - Justificativa:
 * Gerenciamento de usuários é uma função administrativa sensível,
 * restrita à gestão da oficina.
 * <p>
 * /clientes/**
 * - ADMIN, SECRETARIA
 * - Justificativa:
 * Cadastro e manutenção de clientes são tarefas administrativas,
 * não relacionadas à execução técnica dos serviços.
 * <p>
 * /veiculos/**
 * - ADMIN, SECRETARIA
 * - Justificativa:
 * Veículos fazem parte do cadastro operacional da oficina,
 * sendo mantidos pela área administrativa.
 * <p>
 * /servicos/**
 * - ADMIN: CRUD completo
 * - SECRETARIA, MECANICO: apenas leitura (GET)
 * - Justificativa:
 * A definição de serviços, preços e descrições é uma decisão
 * administrativa, enquanto os demais perfis apenas consultam
 * essas informações.
 * <p>
 * /itens-servico/**
 * - ADMIN, SECRETARIA: criação e manutenção
 * - MECANICO: leitura
 * - Justificativa:
 * Itens de serviço compõem a ordem de serviço e são gerenciados
 * administrativamente, enquanto o mecânico apenas consulta
 * para execução.
 * <p>
 * /ordens-servico/**
 * - ADMIN: acesso total
 * - SECRETARIA: criação, edição e acompanhamento
 * - MECANICO: consulta e conclusão de ordens
 * - Justificativa:
 * Ordens de serviço representam o fluxo principal da oficina,
 * envolvendo tanto a parte administrativa quanto a execução técnica.
 * <p>
 * ---------------------------------------------------------
 * OBSERVAÇÕES ARQUITETURAIS
 * ---------------------------------------------------------
 * <p>
 * - As regras de acesso são centralizadas nesta configuração,
 * evitando anotações espalhadas nos controllers.
 * - O controle por método HTTP (GET, POST, PUT, DELETE) permite
 * diferenciar leitura de escrita quando necessário.
 * - Essa abordagem facilita manutenção, auditoria e entendimento
 * do sistema por novos desenvolvedores.
 *
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

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

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

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/usuarios/**").hasAuthority("ADMIN")

                        .requestMatchers("/dashboard/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        .requestMatchers("/clientes/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        .requestMatchers("/veiculos/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        .requestMatchers(HttpMethod.POST, "/servicos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/servicos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/servicos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/servicos/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA", "MECANICO")

                        .requestMatchers(HttpMethod.GET, "/itens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA", "MECANICO")
                        .requestMatchers("/itens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        .requestMatchers(HttpMethod.GET, "/ordens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA", "MECANICO")
                        .requestMatchers(HttpMethod.PUT, "/ordens-servico/concluir/**")
                        .hasAnyAuthority("ADMIN", "MECANICO")
                        .requestMatchers("/ordens-servico/**")
                        .hasAnyAuthority("ADMIN", "SECRETARIA")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
