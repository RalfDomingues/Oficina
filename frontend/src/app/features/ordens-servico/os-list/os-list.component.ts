import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { OrdemServicoService } from '../data/ordem-servico.service';
import { OrdemServico } from '../../../shared/models/ordem-servico.model';
import { PageResponse } from '../../../shared/models/page.model';

import { OsFormDialogComponent } from '../os-form-dialog/os-form-dialog.component';

import { ClienteService } from '../../clientes/data/cliente.service';
import { VeiculoService } from '../../veiculos/data/veiculo.service';
import { Veiculo } from '../../../shared/models/veiculo.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { ValorFinalDialogComponent } from '../../../shared/components/valor-final-dialog/valor-final-dialog.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';



@Component({
  selector: 'app-os-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatDialogModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule

  ],
  templateUrl: './os-list.component.html',
  styleUrl: './os-list.component.scss',
})
export class OsListComponent implements OnInit {
  private osService = inject(OrdemServicoService);
  private clienteService = inject(ClienteService);
  private veiculoService = inject(VeiculoService);
  private dialog = inject(MatDialog);

  displayedColumns = ['id', 'cliente', 'veiculo', 'status', 'descricao', 'dataAbertura', 'acoes'];

  private snack = inject(MatSnackBar);


  // ====== dados ======
  data: OrdemServico[] = [];      // página atual (server mode)
  allData: OrdemServico[] = [];   // tudo (search mode)

  loading = false;

  // paginação server (normal)
  pageIndex = 0;
  pageSize = 10;
  totalElements = 0;

  // paginação local (quando search ativo)
  localPageIndex = 0;
  localPageSize = 10;

  // busca
  search = '';

  clienteNomeById = new Map<number, string>();
  veiculoModeloById = new Map<number, string>();

  ngOnInit(): void {
    this.carregar();
  }

  // ====== helpers de modo ======
  get isSearchMode(): boolean {
    return (this.search ?? '').trim().length > 0;
  }

  // data que vai pra tabela
  get tableData(): OrdemServico[] {
    if (!this.isSearchMode) return this.data;

    const filtered = this.filtrar(this.allData);
    const start = this.localPageIndex * this.localPageSize;
    const end = start + this.localPageSize;
    return filtered.slice(start, end);
  }

  // total que vai pro paginator
  get tableTotal(): number {
    if (!this.isSearchMode) return this.totalElements;
    return this.filtrar(this.allData).length;
  }

  private showError(err: any) {
    const msg =
      err?.error?.message ||
      err?.error?.mensagem ||
      (typeof err?.error === 'string' ? err.error : null) ||
      'Ação não permitida.';

    this.snack.open(msg, 'Fechar', {
      duration: 4500,
      horizontalPosition: 'center',
      verticalPosition: 'top',
    });
  }



  // ====== events ======
  onPage(ev: PageEvent) {
    if (this.isSearchMode) {
      this.localPageIndex = ev.pageIndex;
      this.localPageSize = ev.pageSize;
      return;
    }

    this.pageIndex = ev.pageIndex;
    this.pageSize = ev.pageSize;
    this.carregar();
  }

  onSearchChange() {
    // entrou em search mode: zera pagina local e garante allData carregado
    if (this.isSearchMode) {
      this.localPageIndex = 0;
      if (this.allData.length === 0) this.carregarTudo();
      return;
    }

    // saiu do search mode: volta pro server mode
    this.localPageIndex = 0;
    this.carregar();
  }

  // ====== carregar normal (paginado do backend) ======
  carregar(): void {
    this.loading = true;

    this.osService.listar(this.pageIndex, this.pageSize).subscribe({
      next: (page: PageResponse<OrdemServico>) => {
        const lista = (page.content ?? []).slice();
        lista.sort((a, b) => this.compareOs(a, b));

        this.data = lista;
        this.totalElements = page.totalElements ?? 0;

        const clienteIds = Array.from(new Set(lista.map(o => o.clienteId).filter(Boolean)));
        const veiculoIds = Array.from(new Set(lista.map(o => o.veiculoId).filter(Boolean)));

        forkJoin({
          clientes: clienteIds.length ? this.preencherClientes(clienteIds) : of(void 0),
          veiculos: veiculoIds.length ? this.preencherVeiculos(veiculoIds) : of(void 0),
        }).subscribe({
          next: () => { this.loading = false; },
          error: () => { this.loading = false; }
        });
      },
      error: () => {
        this.data = [];
        this.totalElements = 0;
        this.loading = false;
      }
    });
  }

  // ====== carregar tudo (para search mode) ======
  private carregarTudo(): void {
    this.loading = true;

    // pega tudo de uma vez (110/95 etc). Se teu banco crescer MUITO, depois a gente migra pra server-side search.
    const BIG = 5000;

    this.osService.listar(0, BIG).subscribe({
      next: (page: PageResponse<OrdemServico>) => {
        const lista = (page.content ?? []).slice();
        lista.sort((a, b) => this.compareOs(a, b));
        this.allData = lista;

        const clienteIds = Array.from(new Set(lista.map(o => o.clienteId).filter(Boolean)));
        const veiculoIds = Array.from(new Set(lista.map(o => o.veiculoId).filter(Boolean)));

        forkJoin({
          clientes: clienteIds.length ? this.preencherClientes(clienteIds) : of(void 0),
          veiculos: veiculoIds.length ? this.preencherVeiculos(veiculoIds) : of(void 0),
        }).subscribe({
          next: () => { this.loading = false; },
          error: () => { this.loading = false; }
        });
      },
      error: () => {
        this.allData = [];
        this.loading = false;
      }
    });
  }

  // ====== filtro (busca global) ======
  private filtrar(lista: OrdemServico[]): OrdemServico[] {
    const q = (this.search ?? '').trim().toLowerCase();
    if (!q) return lista;

    return (lista ?? []).filter((os) => {
      const cliente = this.clienteNome(os.clienteId).toLowerCase();
      const veiculo = this.veiculoModelo(os.veiculoId).toLowerCase();

      const id = String(os.id);
      const clienteId = String(os.clienteId);
      const veiculoId = String(os.veiculoId);

      const status = String(os.status ?? '').toLowerCase();
      const descricao = String(os.descricao ?? '').toLowerCase();

      const data = os.dataAbertura
        ? new Date(os.dataAbertura).toLocaleDateString('pt-BR')
        : '';
      const dataLower = data.toLowerCase();

      return (
        id.includes(q) ||
        clienteId.includes(q) ||
        veiculoId.includes(q) ||
        cliente.includes(q) ||
        veiculo.includes(q) ||
        status.includes(q) ||
        descricao.includes(q) ||
        dataLower.includes(q)
      );
    });
  }

  // ====== Exibição ======
  clienteNome(clienteId: number): string {
    return this.clienteNomeById.get(clienteId) ?? `#${clienteId}`;
  }

  veiculoModelo(veiculoId: number): string {
    return this.veiculoModeloById.get(veiculoId) ?? `#${veiculoId}`;
  }


  podeAvancar(os: OrdemServico): boolean {
    return os.status === 'ABERTA' || os.status === 'EM_ANDAMENTO';
  }

  avancarStatus(os: OrdemServico) {
    const next =
      os.status === 'ABERTA' ? 'EM_ANDAMENTO' :
        os.status === 'EM_ANDAMENTO' ? 'CONCLUIDA' :
          null;

    if (!next) return;

    this.osService.atualizar(os.id, { status: next }).subscribe({
      next: () => this.refreshAfterWrite(),
      error: (e) => this.showError(e),
    });
  }


  abrirCriar() {
    const ref = this.dialog.open(OsFormDialogComponent, {
      width: '720px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((value) => {
      if (!value) return;

      this.osService.criar({
        clienteId: Number(value.clienteId),
        veiculoId: Number(value.veiculoId),
        descricao: String(value.descricao),
        valorEstimado: value.valorEstimado ?? null,
      }).subscribe({ next: () => this.refreshAfterWrite() });
    });
  }

  abrirEditar(os: OrdemServico) {
    const ref = this.dialog.open(OsFormDialogComponent, {
      width: '720px',
      data: { mode: 'edit', initial: os },
    });

    ref.afterClosed().subscribe((value) => {
      if (!value) return;

      this.osService.atualizar(os.id, {
        status: value.status,
        descricao: String(value.descricao),
        valor: value.valorFinal ?? null,
      }).subscribe({ next: () => this.refreshAfterWrite() });
    });
  }

  private refreshAfterWrite() {
    // se estiver pesquisando, atualiza allData (pra refletir mudança)
    if (this.isSearchMode) this.carregarTudo();
    else this.carregar();
  }

  // ====== Ordenação default ======
  private compareOs(a: OrdemServico, b: OrdemServico): number {
    const pa = this.statusPriority(a.status);
    const pb = this.statusPriority(b.status);

    if (pa !== pb) return pa - pb;

    // dentro da mesma prioridade: data de abertura asc
    const da = new Date(a.dataAbertura).getTime();
    const db = new Date(b.dataAbertura).getTime();
    if (da !== db) return da - db;

    // empate: id asc
    return a.id - b.id;
  }

  private statusPriority(status: OrdemServico['status']): number {
    if (status === 'EM_ANDAMENTO') return 0;
    if (status === 'ABERTA') return 1;
    if (status === 'CONCLUIDA') return 2;
    if (status === 'CANCELADA') return 3;
    return 9;
  }

  // ====== preenchimento maps ======
  private preencherClientes(ids: number[]) {
    const wanted = new Set(ids);

    return this.clienteService.listarTodos().pipe(
      map((lista: Cliente[]) => {
        this.clienteNomeById.clear();

        for (const c of (lista ?? [])) {
          if (wanted.has(c.id)) {
            this.clienteNomeById.set(c.id, (c as any).nome ?? `#${c.id}`);
          }
        }

        const faltando = ids.filter((id) => !this.clienteNomeById.has(id));
        return faltando;
      }),
      catchError(() => of(ids)),
      switchMap((faltando: number[]) => {
        if (!faltando.length) return of(void 0);

        return forkJoin(
          faltando.map((id) =>
            this.clienteService.buscarPorId(id).pipe(
              map((c: Cliente) => {
                this.clienteNomeById.set(id, (c as any).nome ?? `#${id}`);
                return void 0;
              }),
              catchError(() => {
                this.clienteNomeById.set(id, `#${id}`);
                return of(void 0);
              })
            )
          )
        ).pipe(map(() => void 0));
      })
    );
  }

  private preencherVeiculos(ids: number[]) {
    const wanted = new Set(ids);

    return (this.veiculoService.listarTodos() as any).pipe(
      map((res: any) => {
        const lista: Veiculo[] = Array.isArray(res) ? res : (res?.content ?? []);

        this.veiculoModeloById.clear();

        for (const v of (lista ?? [])) {
          if (wanted.has(v.id)) {
            this.veiculoModeloById.set(v.id, (v as any).modelo ?? `#${v.id}`);
          }
        }

        return void 0;
      }),
      catchError(() => {
        this.veiculoModeloById.clear();
        return of(void 0);
      })
    );
  }

}
