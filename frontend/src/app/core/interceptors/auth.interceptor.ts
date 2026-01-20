import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from '../auth/auth.service';

/**
 * Interceptor responsável por:
 * - Anexar o token JWT nas requisições autenticadas
 * - Tratar erros globais de autenticação/autorização
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    // Endpoints públicos (login, refresh, etc) não recebem Authorization
    if (req.url.includes('/auth/')) {
        return next(req);
    }

    const token = auth.getToken();

    // Clona a request apenas se existir token
    const authReq = token
        ? req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`,
            },
        })
        : req;

    return next(authReq).pipe(
        catchError((err: unknown) => {
            if (err instanceof HttpErrorResponse) {
                /**
                 * 401:
                 * - token expirado ou inválido
                 * - encerra sessão e redireciona para login
                 * - preserva rota atual para redirect pós-login
                 */
                if (err.status === 401) {
                    auth.logout();
                    router.navigate(['/login'], {
                        queryParams: { redirectTo: router.url },
                    });
                }

                /**
                 * 403:
                 * - usuário autenticado porém sem permissão
                 * - redireciona para home
                 */
                if (err.status === 403) {
                    router.navigate(['/home']);
                }
            }

            return throwError(() => err);
        })
    );
};
