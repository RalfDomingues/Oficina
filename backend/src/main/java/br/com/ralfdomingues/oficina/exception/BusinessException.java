package br.com.ralfdomingues.oficina.exception;

/**
 * Exceção utilizada para sinalizar violações de regras de negócio.
 *
 * <p>
 * Deve ser lançada quando uma operação é válida do ponto de vista
 * técnico, mas não permitida pelas regras do domínio.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
