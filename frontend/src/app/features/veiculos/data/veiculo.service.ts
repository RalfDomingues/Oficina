import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../shared/models/page.model';
import { Veiculo, TipoVeiculo } from '../../../shared/models/veiculo.model';

export type VeiculoCreateDTO = {
  placa: string;
  modelo: string;
  marca: string;
  ano: number;
  tipo: TipoVeiculo;
  clienteId: number;
};

export type VeiculoUpdateDTO = {
  modelo?: string | null;
  marca?: string | null;
  ano?: number | null;
  tipo?: TipoVeiculo | null;
  ativo?: boolean | null;
};

@Injectable({ providedIn: 'root' })
export class VeiculoService {
  private readonly baseUrl = `${environment.apiUrl}/veiculos`;

  constructor(private http: HttpClient) {}

  /** Lista veículos (paginado) no padrão Spring Data. */
  listarPaginado(page = 0, size = 10): Observable<PageResponse<Veiculo>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<Veiculo>>(this.baseUrl, { params });
  }

  /** Lista veículos de um cliente (paginado). */
  listarPorClientePaginado(clienteId: number, page = 0, size = 10): Observable<PageResponse<Veiculo>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<Veiculo>>(`${this.baseUrl}/cliente/${clienteId}`, { params });
  }

  /** Busca veículo por id. */
  buscarPorId(id: number): Observable<Veiculo> {
    return this.http.get<Veiculo>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cria veículo.
   * Regra do backend: placa deve ser enviada em MAIÚSCULO.
   */
  criar(dto: VeiculoCreateDTO): Observable<Veiculo> {
    return this.http.post<Veiculo>(this.baseUrl, { ...dto, placa: (dto.placa ?? '').toUpperCase() });
  }

  /** Atualiza veículo (permite soft delete via `ativo`). */
  atualizar(id: number, dto: VeiculoUpdateDTO): Observable<Veiculo> {
    return this.http.put<Veiculo>(`${this.baseUrl}/${id}`, dto);
  }

  /** Exclui/desativa veículo (conforme regra do backend). */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Helper: busca todos os veículos (ativos + inativos) sem paginação completa.
   * Útil para selects/listas locais.
   */
  listarTodos(size = 5000): Observable<Veiculo[]> {
    return this.listarPaginado(0, size).pipe(map((p) => p.content ?? []));
  }

  /**
   * Helper: busca todos os veículos de um cliente (ativos + inativos).
   */
  listarTodosPorCliente(clienteId: number, size = 5000): Observable<Veiculo[]> {
    return this.listarPorClientePaginado(clienteId, 0, size).pipe(map((p) => p.content ?? []));
  }
}
