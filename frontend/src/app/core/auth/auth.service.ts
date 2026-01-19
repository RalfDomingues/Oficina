import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from './auth.model';
import { environment } from '../../../environments/environment';

export type Perfil = 'ADMIN' | 'SECRETARIA' | 'MECANICO';

export type CurrentUser = {
    id: number;
    nome: string;
    email: string;
    perfil: Perfil;
};

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly TOKEN_KEY = 'mf_token';
    private readonly USER_KEY = 'mf_user';

    // TODO: mover p/ environment depois
    private readonly API = environment.apiUrl;

    constructor(private http: HttpClient) { }

    login(payload: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.API}/auth/login`, payload).pipe(
            tap((res) => {
                localStorage.setItem(this.TOKEN_KEY, res.token);

                const user: CurrentUser = {
                    id: res.id,
                    nome: res.nome,
                    email: res.email,
                    perfil: res.perfil as Perfil,
                };

                localStorage.setItem(this.USER_KEY, JSON.stringify(user));
            })
        );
    }

    logout(): void {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    getUser(): CurrentUser | null {
        const raw = localStorage.getItem(this.USER_KEY);
        if (!raw) return null;

        try {
            return JSON.parse(raw) as CurrentUser;
        } catch {
            return null;
        }
    }

    getPerfil(): Perfil | null {
        return this.getUser()?.perfil ?? null;
    }

    hasRole(role: Perfil): boolean {
        return this.getPerfil() === role;
    }

    hasAnyRole(...roles: Perfil[]): boolean {
        const perfil = this.getPerfil();
        return !!perfil && roles.includes(perfil);
    }

    isLoggedIn(): boolean {
        const token = this.getToken();
        if (!token) return false;

        if (this.isTokenExpired(token)) {
            this.logout();
            return false;
        }

        return true;
    }

    private decodeJwtPayload(token: string): any | null {
        try {
            const base64Url = token.split('.')[1];
            if (!base64Url) return null;

            // base64url -> base64
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
                + '==='.slice((base64Url.length + 3) % 4);

            const json = decodeURIComponent(
                atob(base64)
                    .split('')
                    .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            );

            return JSON.parse(json);
        } catch {
            return null;
        }
    }

    isTokenExpired(token?: string | null): boolean {
        const t = token ?? this.getToken();
        if (!t) return true;

        const payload = this.decodeJwtPayload(t);
        if (!payload) return false; // não “chuta” como expirado se não conseguir ler

        const exp: number | undefined = payload?.exp;
        if (!exp) return false;

        const nowSeconds = Math.floor(Date.now() / 1000);
        return nowSeconds >= exp;
    }
}
