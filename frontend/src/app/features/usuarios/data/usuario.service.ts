import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../shared/models/page.model';
import { PerfilUsuario, Usuario } from '../../../shared/models/usuario.model';

export type UsuarioCreateDTO = {
    nome: string;
    email: string;
    senha: string;
    perfil: PerfilUsuario;
};

export type UsuarioUpdateDTO = {
    nome?: string | null;
    email?: string | null;
    senha?: string | null;
    perfil?: PerfilUsuario | null;
    ativo?: boolean | null;
};

@Injectable({ providedIn: 'root' })
export class UsuarioService {
    private readonly baseUrl = `${environment.apiUrl}/usuarios`;

    constructor(private http: HttpClient) { }

    listar(page = 0, size = 10): Observable<PageResponse<Usuario>> {
        const params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size))
            .set('sort', 'id,desc');

        return this.http.get<PageResponse<Usuario>>(this.baseUrl, { params });
    }

    criar(dto: UsuarioCreateDTO): Observable<Usuario> {
        return this.http.post<Usuario>(this.baseUrl, dto);
    }

    atualizar(id: number, dto: UsuarioUpdateDTO): Observable<Usuario> {
        return this.http.put<Usuario>(`${this.baseUrl}/${id}`, dto);
    }

    desativar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}
