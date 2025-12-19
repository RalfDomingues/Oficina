package br.com.ralfdomingues.oficina.exception;

/**
 * Exceção utilizada para indicar que um recurso não foi encontrado.
 *
 * <p>
 * Deve ser lançada quando a entidade ou recurso solicitado
 * não existe ou não está disponível no contexto atual.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
