import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  listar(page: number, size: number) {
    return this.http.get<PageResponse<OrdemServico>>(
      `${this.baseUrl}/ordens-servico?page=${page}&size=${size}`
    );
  }

  buscar(id: number) {
    return this.http.get<OrdemServico>(`${this.baseUrl}/ordens-servico/${id}`);
  }

  criar(dto: OrdemServicoCreateDTO) {
    return this.http.post<OrdemServico>(`${this.baseUrl}/ordens-servico`, dto);
  }

  atualizar(id: number, dto: OrdemServicoUpdateDTO) {
    return this.http.put<OrdemServico>(`${this.baseUrl}/ordens-servico/${id}`, dto);
  }

  cancelar(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/ordens-servico/${id}`);
  }

  concluir(id: number) {
    return this.atualizar(id, { status: 'CONCLUIDA' });
  }
}
