import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    // não adiciona token em endpoints públicos
    if (req.url.includes('/auth/')) {
        return next(req);
    }

    const token = auth.getToken();

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
                // Token expirou ou inválido -> derruba sessão e manda pro login
                if (err.status === 401) {
                    auth.logout();
                    router.navigate(['/login'], {
                        queryParams: { redirectTo: router.url },
                    });
                }

                // Sem permissão -> manda pra home
                if (err.status === 403) {
                    router.navigate(['/home']);
                }
            }

            return throwError(() => err);
        })
    );
};
