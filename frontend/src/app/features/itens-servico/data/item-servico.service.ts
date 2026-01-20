import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

export interface ItemServico {
  id: number;
  ordemServicoId: number;
  servicoId: number;
  nomeServico: string;
  valorUnitario: number;
  quantidade: number;
  valorTotal: number;
  ativo: boolean;
}

export interface ItemServicoCreateDTO {
  servicoId: number;
  ordemServicoId: number;
  quantidade: number;
  valor: number;
}

export interface ItemServicoUpdateDTO {
  servicoId?: number;
  quantidade?: number;
  valor?: number;
  ativo?: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ItemServicoService {
  private readonly baseUrl = `${environment.apiUrl}/itens-servico`;

  constructor(private http: HttpClient) {}

  /**
   * Lista itens paginados e retorna apenas o content.
   * Se a tela precisar do totalElements/totalPages, crie um método que retorne Page<T>.
   */
  listar(page = 0, size = 10): Observable<ItemServico[]> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));

    return this.http
      .get<Page<ItemServico>>(this.baseUrl, { params })
      .pipe(map((response) => response.content ?? []));
  }

  /** Helper para telas que precisam de uma carga "grande" sem paginação completa. */
  listarTodos(): Observable<ItemServico[]> {
    return this.listar(0, 5000);
  }

  /**
   * Lista itens vinculados a uma OS (paginado) e retorna apenas o content.
   * Endpoint: /itens-servico/ordem/{ordemId}
   */
  listarPorOrdem(ordemId: number, page = 0, size = 10): Observable<ItemServico[]> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));

    return this.http
      .get<Page<ItemServico>>(`${this.baseUrl}/ordem/${ordemId}`, { params })
      .pipe(map((response) => response.content ?? []));
  }

  /** Busca item por id. */
  buscarPorId(id: number): Observable<ItemServico> {
    return this.http.get<ItemServico>(`${this.baseUrl}/${id}`);
  }

  /** Cria item de serviço. */
  criar(dto: ItemServicoCreateDTO): Observable<ItemServico> {
    return this.http.post<ItemServico>(this.baseUrl, dto);
  }

  /** Atualiza item de serviço. */
  atualizar(id: number, dto: ItemServicoUpdateDTO): Observable<ItemServico> {
    return this.http.put<ItemServico>(`${this.baseUrl}/${id}`, dto);
  }

  /** Remove item (conforme regra do backend: delete físico ou soft delete). */
  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
