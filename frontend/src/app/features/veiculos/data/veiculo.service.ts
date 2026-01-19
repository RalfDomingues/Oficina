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

  constructor(private http: HttpClient) { }

  listarPaginado(page = 0, size = 10): Observable<PageResponse<Veiculo>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<Veiculo>>(this.baseUrl, { params });
  }

  listarPorClientePaginado(clienteId: number, page = 0, size = 10): Observable<PageResponse<Veiculo>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<Veiculo>>(`${this.baseUrl}/cliente/${clienteId}`, { params });
  }

  buscarPorId(id: number): Observable<Veiculo> {
    return this.http.get<Veiculo>(`${this.baseUrl}/${id}`);
  }

  criar(dto: VeiculoCreateDTO): Observable<Veiculo> {
    // placa no back exige MAIÚSCULO
    return this.http.post<Veiculo>(this.baseUrl, { ...dto, placa: (dto.placa ?? '').toUpperCase() });
  }

  atualizar(id: number, dto: VeiculoUpdateDTO): Observable<Veiculo> {
    return this.http.put<Veiculo>(`${this.baseUrl}/${id}`, dto);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Se tu ainda precisar em algum lugar (select), dá pra manter:
  listarTodos(size = 5000): Observable<Veiculo[]> {
    return this.listarPaginado(0, size).pipe(map((p) => p.content ?? []));
  }
}
