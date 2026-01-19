package br.com.ralfdomingues.oficina.controller.handler;

import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções da API.
 *
 * <p>
 * Centraliza o tratamento de erros e garante respostas HTTP
 * padronizadas para todos os endpoints da aplicação.
 *
 * <p>
 * A API adota um contrato consistente de erro contendo:
 * <ul>
 *   <li>Timestamp da ocorrência</li>
 *   <li>Status HTTP</li>
 *   <li>Descrição padrão do erro</li>
 *   <li>Mensagem funcional para o cliente</li>
 *   <li>Detalhes de validação quando aplicável</li>
 * </ul>
 *
 * <p>
 * Essa abordagem facilita o consumo da API pelo frontend
 * e simplifica a manutenção do tratamento de erros.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata falhas de autenticação causadas por credenciais inválidas.
     *
     * @return resposta HTTP 401 com mensagem padronizada
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = buildBody(
                HttpStatus.UNAUTHORIZED,
                "Credenciais inválidas"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Trata falhas genéricas de autenticação.
     *
     * @return resposta HTTP 401
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex) {
        Map<String, Object> body = buildBody(
                HttpStatus.UNAUTHORIZED,
                "Falha na autenticação"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Trata erros de recurso não encontrado.
     *
     * @return resposta HTTP 404
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        Map<String, Object> body = buildBody(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Trata violações de regras de negócio.
     *
     * @return resposta HTTP 400
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusiness(BusinessException ex) {
        Map<String, Object> body = buildBody(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Trata erros de validação de dados de entrada.
     *
     * <p>
     * Retorna os erros agrupados por campo para facilitar
     * o consumo pelo frontend.
     *
     * @return resposta HTTP 400 com detalhes de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> body = buildBody(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Trata exceções não previstas, evitando exposição de detalhes internos.
     *
     * <p>
     * O stack trace é registrado para análise interna,
     * enquanto o cliente recebe uma mensagem genérica.
     *
     * @return resposta HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex) {

        Map<String, Object> body = buildBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor"
        );

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Constrói o corpo padrão de erro sem detalhes adicionais.
     */
    private Map<String, Object> buildBody(HttpStatus status, String message) {
        return buildBody(status, message, null);
    }

    /**
     * Constrói o corpo padrão de erro da API.
     *
     * @param status  status HTTP
     * @param message mensagem funcional
     * @param errors  detalhes opcionais (ex: validações)
     * @return mapa representando o erro
     */
    private Map<String, Object> buildBody(
            HttpStatus status,
            String message,
            Map<String, Object> errors) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        if (errors != null) {
            body.put("errors", errors);
        }

        return body;
    }
}
