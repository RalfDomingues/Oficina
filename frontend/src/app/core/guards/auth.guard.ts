import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) return true;

  // manda pro login e preserva pra onde o usuário tentou ir
  router.navigate(['/login'], { queryParams: { redirectTo: state.url } });
  return false;
};
