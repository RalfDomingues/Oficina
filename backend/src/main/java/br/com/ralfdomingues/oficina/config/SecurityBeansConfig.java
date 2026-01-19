package br.com.ralfdomingues.oficina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração centralizada de beans relacionados à segurança.
 *
 * <p>
 * Esta classe expõe componentes reutilizáveis do Spring Security
 * que não pertencem diretamente à configuração de controle de acesso,
 * mantendo separação clara de responsabilidades.
 *
 * <p>
 * A definição explícita do {@link PasswordEncoder} garante:
 * <ul>
 *   <li>Padronização do algoritmo de hash de senha em toda a aplicação</li>
 *   <li>Facilidade de troca futura do algoritmo, se necessário</li>
 *   <li>Desacoplamento entre regras de negócio e implementação criptográfica</li>
 * </ul>
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Encoder de senhas utilizado no armazenamento e validação
     * de credenciais de usuários.
     *
     * <p>
     * Este bean é injetado automaticamente nos serviços responsáveis
     * por criação e autenticação de usuários.
     *
     * @return implementação de {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
