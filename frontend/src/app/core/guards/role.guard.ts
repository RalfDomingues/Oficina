import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, Perfil } from '../auth/auth.service';

/**
 * Guard de autorização por perfil.
 *
 * - Se não estiver autenticado: redireciona para login preservando a rota.
 * - Se autenticado e possuir ao menos um dos perfis permitidos: libera acesso.
 * - Caso contrário: redireciona para home.
 */
export const roleGuard = (allowed: Perfil[]): CanActivateFn => {
    return (_route, state) => {
        const auth = inject(AuthService);
        const router = inject(Router);

        if (!auth.isLoggedIn()) {
            router.navigate(['/login'], { queryParams: { redirectTo: state.url } });
            return false;
        }

        if (auth.hasAnyRole(...allowed)) return true;

        router.navigate(['/home']);
        return false;
    };
};
