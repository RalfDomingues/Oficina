import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, of } from 'rxjs';
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

export type ClienteUpdateDTO = Partial<Omit<ClienteCreateDTO, 'cpf'>>; // ✅ não atualiza cpf


@Injectable({ providedIn: 'root' })
export class ClienteService {
  private readonly baseUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) { }

  /**
   * Lista paginada (padrão Spring Data: /clientes?page=0&size=10)
   * Se o backend NÃO for paginado, use listarTodos().
   */
  listarPaginado(page = 0, size = 10): Observable<PageResponse<Cliente>> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PageResponse<Cliente>>(this.baseUrl, { params });
  }

  /**
   * Lista todos (backend retornando array direto)
   * Mantém seu método original, mas com nome mais claro.
   */
  listarTodos(size = 500): Observable<Cliente[]> {
    // Como o backend é paginado, buscamos uma página grande e devolvemos o content.
    // Se um dia tiver mais de 500 clientes, aumenta esse size ou implementa paginação completa.
    return this.listarPaginado(0, size).pipe(
      map((p) => p?.content ?? [])
    );
  }

  /**
   * Busca por id
   */
  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cria cliente
   */
  criar(dto: ClienteCreateDTO): Observable<Cliente> {
    return this.http.post<Cliente>(this.baseUrl, dto);
  }

  /**
   * Atualiza cliente
   */
  atualizar(id: number, dto: ClienteUpdateDTO): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${id}`, dto);
  }

  /**
   * Excluir (mantém)
   */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Helper: retorna só clientes "ativos".
   * - Se seu model não tiver `ativo`, ele devolve todos.
   * - Se você estiver usando listarPaginado no backend, prefere usar listarAtivosPaginado.
   */
  listarAtivos(): Observable<Cliente[]> {
    return this.listarTodos().pipe(
      map((lista) => (lista ?? []).filter((c: any) => c?.ativo !== false))
    );
  }

  /**
   * Helper: ativos paginado (se seu backend já for paginado).
   * Filtra no front, mas mantém PageResponse coerente.
   */
  listarAtivosPaginado(page = 0, size = 10): Observable<PageResponse<Cliente>> {
    return this.listarPaginado(page, size).pipe(
      map((p) => ({
        ...p,
        content: (p.content ?? []).filter((c: any) => c?.ativo !== false),
      }))
    );
  }

  /**
   * Helper: monta um Map id -> nome (pra tabela de OS)
   */
  mapIdNome(lista: Cliente[]): Map<number, string> {
    const m = new Map<number, string>();
    (lista ?? []).forEach((c: any) => {
      if (c?.id != null) m.set(Number(c.id), c?.nome ?? `#${c.id}`);
    });
    return m;
  }
}
