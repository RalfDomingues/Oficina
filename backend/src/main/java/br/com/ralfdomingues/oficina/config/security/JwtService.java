package br.com.ralfdomingues.oficina.config.security;

import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Serviço responsável pela geração e validação de tokens JWT utilizados
 * na autenticação stateless da aplicação.
 *
 * <p>
 * Este serviço define o contrato básico do token, incluindo:
 * <ul>
 *   <li>Identificador do usuário (email) como subject</li>
 *   <li>Perfil do usuário como claim</li>
 *   <li>Tempo de expiração configurável</li>
 * </ul>
 *
 * <p>
 * A lógica de autorização não é responsabilidade deste serviço;
 * ele apenas garante a integridade e a extração de informações do token.
 */
@Service
public class JwtService {

    /**
     * Chave secreta utilizada para assinatura do token JWT.
     * Deve possuir tamanho adequado para o algoritmo HMAC utilizado.
     */
    @Value("${security.jwt.secret}")
    private String secret;

    /**
     * Tempo de expiração do token em milissegundos.
     */
    @Value("${security.jwt.expiration}")
    private Long expiration;

    /**
     * Constrói a chave de assinatura a partir do segredo configurado.
     *
     * <p>
     * A conversão explícita para UTF-8 garante consistência entre ambientes.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * <p>
     * O email é utilizado como subject por ser um identificador único
     * no domínio da aplicação.
     *
     * @param usuario usuário autenticado
     * @return token JWT assinado
     */
    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("perfil", usuario.getPerfil().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrai o email (subject) de um token JWT válido.
     *
     * <p>
     * Tokens inválidos ou expirados geram exceções que são tratadas
     * em camadas superiores do fluxo de segurança.
     *
     * @param token token JWT recebido na requisição
     * @return email associado ao token
     */
    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
