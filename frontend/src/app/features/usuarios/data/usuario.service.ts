import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

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

    /**
     * Lista usuários de forma paginada.
     * Permite filtrar por ativo/inativo quando o parâmetro é informado.
     */
    listar(page = 0, size = 10, ativo?: boolean): Observable<PageResponse<Usuario>> {
        let params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size))
            .set('sort', 'nome,asc');

        if (ativo !== undefined) {
            params = params.set('ativo', String(ativo));
        }

        return this.http.get<PageResponse<Usuario>>(this.baseUrl, { params });
    }

    /**
     * Helper para telas que precisam carregar todos os usuários de uma vez
     * (ex: selects, busca local, permissões).
     */
    listarTodos(size = 5000): Observable<Usuario[]> {
        return this.listar(0, size).pipe(map((page) => page.content ?? []));
    }

    /** Cria um novo usuário. */
    criar(dto: UsuarioCreateDTO): Observable<Usuario> {
        return this.http.post<Usuario>(this.baseUrl, dto);
    }

    /**
     * Atualiza dados do usuário.
     * Pode alterar perfil, senha e também controlar o status ativo.
     */
    atualizar(id: number, dto: UsuarioUpdateDTO): Observable<Usuario> {
        return this.http.put<Usuario>(`${this.baseUrl}/${id}`, dto);
    }

    /**
     * Desativa usuário.
     * No backend geralmente representa um soft delete.
     */
    desativar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    /**
     * Reativa um usuário previamente desativado.
     */
    reativar(id: number): Observable<Usuario> {
        return this.http.put<Usuario>(`${this.baseUrl}/${id}`, { ativo: true });
    }

    /** Busca usuário por id. */
    buscarPorId(id: number): Observable<Usuario> {
        return this.http.get<Usuario>(`${this.baseUrl}/${id}`);
    }
}
