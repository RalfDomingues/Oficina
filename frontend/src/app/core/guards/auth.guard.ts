import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { AuthService } from '../auth/auth.service';

/**
 * Guard de autenticação.
 *
 * - Permite acesso apenas para usuários logados
 * - Caso contrário, redireciona para o login preservando a rota de destino
 */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) return true;

  router.navigate(['/login'], { queryParams: { redirectTo: state.url } });
  return false;
};
