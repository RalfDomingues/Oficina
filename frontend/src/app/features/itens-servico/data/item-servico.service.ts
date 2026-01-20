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

@Injectable({
    providedIn: 'root'
})
export class ItemServicoService {
    private readonly API_URL = `${environment.apiUrl}/itens-servico`;

    constructor(private http: HttpClient) { }

    listar(page: number = 0, size: number = 10): Observable<ItemServico[]> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<Page<ItemServico>>(this.API_URL, { params })
            .pipe(
                map((response) => response.content ?? [])
            );
    }

    listarPorOrdem(ordemId: number, page: number = 0, size: number = 10): Observable<ItemServico[]> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<Page<ItemServico>>(`${this.API_URL}/ordem/${ordemId}`, { params })
            .pipe(
                map((response) => response.content ?? [])
            );
    }

    buscarPorId(id: number): Observable<ItemServico> {
        return this.http.get<ItemServico>(`${this.API_URL}/${id}`);
    }

    criar(dto: ItemServicoCreateDTO): Observable<ItemServico> {
        return this.http.post<ItemServico>(this.API_URL, dto);
    }

    atualizar(id: number, dto: ItemServicoUpdateDTO): Observable<ItemServico> {
        return this.http.put<ItemServico>(`${this.API_URL}/${id}`, dto);
    }

    deletar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}