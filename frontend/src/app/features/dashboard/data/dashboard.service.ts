import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  OrdemStatusResumo,
  FaturamentoResumo,
  ServicoMaisUsado,
  OrdensPorMes,
} from './dashboard.models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly baseUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  /** Retorna a quantidade de ordens agrupadas por status. */
  ordensPorStatus(): Observable<OrdemStatusResumo[]> {
    return this.http.get<OrdemStatusResumo[]>(`${this.baseUrl}/ordens-por-status`);
  }

  /** Retorna o faturamento total consolidado. */
  faturamentoTotal(): Observable<FaturamentoResumo> {
    return this.http.get<FaturamentoResumo>(`${this.baseUrl}/faturamento-total`);
  }

  /** Retorna os serviços mais utilizados (ranking). */
  servicosMaisUsados(): Observable<ServicoMaisUsado[]> {
    return this.http.get<ServicoMaisUsado[]>(`${this.baseUrl}/servicos-mais-usados`);
  }

  /** Retorna a quantidade de ordens por mês (formato YYYY-MM). */
  ordensPorMes(): Observable<OrdensPorMes[]> {
    return this.http.get<OrdensPorMes[]>(`${this.baseUrl}/ordens-por-mes`);
  }
}
