package br.com.ralfdomingues.oficina.domain.usuario.enums;


/**
 * Perfis de acesso disponíveis no sistema.
 *
 * <p>Define os níveis de permissão utilizados
 * para controle de acesso às funcionalidades.</p>
 */
public enum PerfilUsuario {
    ADMIN,       //acesso total ao sistema
    SECRETARIA,  //clientes, veículos, ordens de serviço
    MECANICO     //ordens de serviço e serviços
}
