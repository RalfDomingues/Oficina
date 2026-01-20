import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Servico } from '../../../shared/models/servico.model';
import { PageResponse } from '../../../shared/models/page.model';
import { environment } from '../../../../environments/environment';

export type ServicoCreateDTO = {
  nome: string;
  preco: number;
};

export type ServicoUpdateDTO = {
  nome?: string | null;
  preco?: number | null;
  ativo?: boolean | null;
};

@Injectable({ providedIn: 'root' })
export class ServicoService {
  private readonly baseUrl = `${environment.apiUrl}/servicos`;

  constructor(private http: HttpClient) { }

  listar(page = 0, size = 10): Observable<PageResponse<Servico>> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', 'nome,asc');

    return this.http.get<PageResponse<Servico>>(this.baseUrl, { params });
  }

  criar(dto: ServicoCreateDTO): Observable<Servico> {
    return this.http.post<Servico>(this.baseUrl, dto);
  }

  atualizar(id: number, dto: ServicoUpdateDTO): Observable<Servico> {
    return this.http.put<Servico>(`${this.baseUrl}/${id}`, dto);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Busca TODOS os serviços (ativos + inativos) sem paginação
  listarTodos(size = 5000): Observable<Servico[]> {
    return this.listar(0, size).pipe(map((p) => p.content ?? []));
  }
}