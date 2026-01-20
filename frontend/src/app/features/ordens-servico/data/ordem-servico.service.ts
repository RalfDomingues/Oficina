import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../shared/models/page.model';
import { OrdemServico, StatusOrdemServico } from '../../../shared/models/ordem-servico.model';

export type OrdemServicoCreateDTO = {
  clienteId: number;
  veiculoId: number;
  descricao: string;
  valorEstimado?: number | null;
};

export type OrdemServicoUpdateDTO = {
  status?: StatusOrdemServico;
  descricao?: string;
  valor?: number | null;
};

@Injectable({ providedIn: 'root' })
export class OrdemServicoService {
  private readonly baseUrl = `${environment.apiUrl}/ordens-servico`;

  constructor(private http: HttpClient) { }

  /**
   * Lista paginada no padrão Spring Data (/ordens-servico?page=0&size=10).
   * Retorna PageResponse para telas que precisam de totalElements.
   */
  listar(page: number, size: number): Observable<PageResponse<OrdemServico>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<OrdemServico>>(this.baseUrl, { params });
  }

  /**
   * Helper para telas que carregam uma lista "grande" sem paginação completa.
   * Se houver chance real de passar do limite, migrar para paginação completa na tela.
   */
  listarTodos(size = 5000): Observable<OrdemServico[]> {
    return this.listar(0, size).pipe(map((response) => response.content ?? []));
  }

  /** Busca OS por id. */
  buscar(id: number): Observable<OrdemServico> {
    return this.http.get<OrdemServico>(`${this.baseUrl}/${id}`);
  }

  /** Cria OS. */
  criar(dto: OrdemServicoCreateDTO): Observable<OrdemServico> {
    return this.http.post<OrdemServico>(this.baseUrl, dto);
  }

  /** Atualiza OS (status/descrição/valor). */
  atualizar(id: number, dto: OrdemServicoUpdateDTO): Observable<OrdemServico> {
    return this.http.put<OrdemServico>(`${this.baseUrl}/${id}`, dto);
  }

  /** Cancela/exclui OS (conforme regra do backend). */
  cancelar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /** Atalho para marcar como concluída. */
  concluir(id: number): Observable<OrdemServico> {
    return this.atualizar(id, { status: 'CONCLUIDA' });
  }
}
