import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, Perfil } from '../auth/auth.service';

export const roleGuard = (allowed: Perfil[]): CanActivateFn => {
    return (_route, state) => {
        const auth = inject(AuthService);
        const router = inject(Router);

        // se não estiver logado, manda pro login e guarda rota de retorno
        if (!auth.isLoggedIn()) {
            router.navigate(['/login'], { queryParams: { redirectTo: state.url } });
            return false;
        }

        // se tem permissão, libera
        if (auth.hasAnyRole(...allowed)) return true;

        // sem permissão -> manda pra home
        router.navigate(['/home']);
        return false;
    };
};
