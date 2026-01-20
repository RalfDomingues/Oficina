import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Cliente } from '../../../shared/models/cliente.model';
import { PageResponse } from '../../../shared/models/page.model';

export type ClienteCreateDTO = {
  nome: string;
  cpf: string;
  email?: string | null;
  telefone?: string | null;
  ativo?: boolean;
};

export type ClienteUpdateDTO = Partial<Omit<ClienteCreateDTO, 'cpf'>>;

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private readonly baseUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) {}

  /**
   * Lista paginada no padrão Spring Data (/clientes?page=0&size=10).
   * Use quando a tela trabalha com paginação no backend.
   */
  listarPaginado(page = 0, size = 10): Observable<PageResponse<Cliente>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<Cliente>>(this.baseUrl, { params });
  }

  /**
   * Lista "todos" usando uma página grande e retornando apenas o content.
   * Se houver chance real de passar do limite, a tela deve migrar para paginação completa.
   */
  listarTodos(size = 500): Observable<Cliente[]> {
    return this.listarPaginado(0, size).pipe(map((p) => p?.content ?? []));
  }

  /** Busca cliente por id. */
  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${id}`);
  }

  /** Cria cliente. */
  criar(dto: ClienteCreateDTO): Observable<Cliente> {
    return this.http.post<Cliente>(this.baseUrl, dto);
  }

  /**
   * Atualiza cliente.
   * CPF não entra no DTO de update para evitar alterações em um identificador sensível.
   */
  atualizar(id: number, dto: ClienteUpdateDTO): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${id}`, dto);
  }

  /** Exclui cliente (conforme regra do backend: delete físico ou soft delete). */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Helper: retorna apenas clientes ativos.
   * Se o campo `ativo` não existir no payload, considera como ativo.
   */
  listarAtivos(): Observable<Cliente[]> {
    return this.listarTodos().pipe(map((lista) => (lista ?? []).filter((c: any) => c?.ativo !== false)));
  }

  /**
   * Helper: filtra ativos mantendo o PageResponse.
   * Observação: o totalElements continua sendo o total do backend; aqui é só utilitário para telas simples.
   */
  listarAtivosPaginado(page = 0, size = 10): Observable<PageResponse<Cliente>> {
    return this.listarPaginado(page, size).pipe(
      map((p) => ({
        ...p,
        content: (p.content ?? []).filter((c: any) => c?.ativo !== false),
      }))
    );
  }

  /** Helper: monta um Map id -> nome (útil para dropdowns/tabelas). */
  mapIdNome(lista: Cliente[]): Map<number, string> {
    const m = new Map<number, string>();
    (lista ?? []).forEach((c: any) => {
      if (c?.id != null) m.set(Number(c.id), c?.nome ?? `#${c.id}`);
    });
    return m;
  }
}
