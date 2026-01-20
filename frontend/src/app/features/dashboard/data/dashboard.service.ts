import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { from, Observable } from 'rxjs';
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

    constructor(private http: HttpClient) { }

    ordensPorStatus(): Observable<OrdemStatusResumo[]> {
        return this.http.get<OrdemStatusResumo[]>(`${this.baseUrl}/ordens-por-status`);
    }

    faturamentoTotal(): Observable<FaturamentoResumo> {
        return this.http.get<FaturamentoResumo>(`${this.baseUrl}/faturamento-total`);
    }

    servicosMaisUsados(): Observable<ServicoMaisUsado[]> {
        return this.http.get<ServicoMaisUsado[]>(`${this.baseUrl}/servicos-mais-usados`);
    }

    ordensPorMes(): Observable<OrdensPorMes[]> {
        return this.http.get<OrdensPorMes[]>(`${this.baseUrl}/ordens-por-mes`);
    }
}
