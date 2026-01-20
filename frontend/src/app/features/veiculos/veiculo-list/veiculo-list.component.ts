import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { Veiculo } from '../../../shared/models/veiculo.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { VeiculoService } from '../data/veiculo.service';
import { VeiculoFormDialogComponent } from '../veiculo-form-dialog/veiculo-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ClienteService } from '../../clientes/data/cliente.service';

type VeiculoView = Veiculo & { clienteNome?: string | null };

@Component({
  selector: 'app-veiculo-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: './veiculo-list.component.html',
  styleUrl: './veiculo-list.component.scss',
})
export class VeiculoListComponent implements OnInit {
  private readonly veiculoService = inject(VeiculoService);
  private readonly clienteService = inject(ClienteService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);

  displayedColumns = ['placa', 'modelo', 'marca', 'ano', 'tipo', 'clienteNome', 'acoes'];

  veiculosAtivos = new MatTableDataSource<VeiculoView>([]);
  veiculosInativos = new MatTableDataSource<VeiculoView>([]);
  abaSelecionada = 0;

  totalAtivosElementos = 0;
  totalInativosElementos = 0;

  paginaIndiceAtivos = 0;
  tamanhoPaginaAtivos = 10;

  paginaIndiceInativos = 0;
  tamanhoPaginaInativos = 10;

  loading = false;
  search = '';

  todosAtivos: VeiculoView[] = [];
  todosInativos: VeiculoView[] = [];

  veiculosAtivosFiltrados: VeiculoView[] = [];
  veiculosInativosFiltrados: VeiculoView[] = [];

  clienteIdFiltro: number | null = null;
  clientesFiltro: Cliente[] = [];

  /** Cache id->nome para evitar várias chamadas repetidas no buscarPorId. */
  private readonly clienteNomeCache = new Map<number, string>();

  ngOnInit(): void {
    this.carregarClientesFiltro();
    this.carregar();
  }

  /** Carrega lista de clientes (ativos) para o filtro por cliente. */
  private carregarClientesFiltro(): void {
    this.clienteService.listarAtivos().subscribe({
      next: (lista) => {
        this.clientesFiltro = this.ordenarClientes(lista ?? []);
      },
      error: () => {
        this.clientesFiltro = [];
      },
    });
  }

  private ordenarClientes(lista: Cliente[]): Cliente[] {
    return [...(lista ?? [])].sort((a, b) =>
      (a?.nome ?? '').localeCompare((b?.nome ?? ''), 'pt-BR', { sensitivity: 'base' })
    );
  }

  /** Aplica o filtro de cliente e recarrega do backend. */
  onClienteFiltroChange(clienteId: number | null): void {
    this.clienteIdFiltro = clienteId ?? null;
    this.carregar();
  }

  /**
   * Carrega veículos (todos ou por cliente) e enriquece com clienteNome.
   * Depois separa em ativos/inativos + aplica busca/paginação local.
   */
  carregar(): void {
    this.loading = true;

    const obs =
      this.clienteIdFiltro != null
        ? this.veiculoService.listarTodosPorCliente(this.clienteIdFiltro)
        : this.veiculoService.listarTodos();

    obs.subscribe({
      next: (list: Veiculo[]) => {
        const raw = (list ?? []) as VeiculoView[];

        this.preencherClienteNome(raw).subscribe({
          next: (enriquecidos) => {
            this.separaEOrdena(enriquecidos);
            this.aplicarFiltro();
            this.loading = false;
          },
          error: () => {
            this.separaEOrdena(raw);
            this.aplicarFiltro();
            this.loading = false;
          },
        });
      },
      error: () => {
        this.loading = false;
        this.snack.open('Erro ao carregar veiculos', 'Fechar', { duration: 3000 });
      },
    });
  }

  /**
   * Enriquecimento de lista: resolve clienteNome por clienteId usando cache.
   * Para ids não cacheados, faz forkJoin em buscarPorId (com fallback).
   */
  private preencherClienteNome(list: VeiculoView[]) {
    const ids = Array.from(
      new Set(
        (list ?? [])
          .map((v) => v.clienteId)
          .filter((id): id is number => typeof id === 'number' && id > 0)
      )
    );

    const missing = ids.filter((id) => !this.clienteNomeCache.has(id));

    if (missing.length === 0) {
      return of(
        (list ?? []).map((v) => ({
          ...v,
          clienteNome: v.clienteId ? this.clienteNomeCache.get(v.clienteId) ?? null : null,
        }))
      );
    }

    const reqs = missing.map((id) =>
      this.clienteService.buscarPorId(id).pipe(
        map((c: any) => ({ id, nome: (c?.nome ?? '').trim() })),
        catchError(() => of({ id, nome: '' }))
      )
    );

    return forkJoin(reqs).pipe(
      map((items) => {
        items.forEach(({ id, nome }) => {
          if (nome) this.clienteNomeCache.set(id, nome);
        });

        return (list ?? []).map((v) => ({
          ...v,
          clienteNome: v.clienteId ? this.clienteNomeCache.get(v.clienteId) ?? null : null,
        }));
      })
    );
  }

  /** Separa ativos/inativos e mantém ordenação padrão. */
  private separaEOrdena(list: VeiculoView[]): void {
    const ativos = list.filter((v) => v.ativo);
    const inativos = list.filter((v) => !v.ativo);

    this.todosAtivos = this.ordenarVeiculos(ativos);
    this.todosInativos = this.ordenarVeiculos(inativos);
  }

  /**
   * Ordena por clienteNome e depois por modelo.
   * Ajuda a manter a lista “agrupada” visualmente.
   */
  private ordenarVeiculos(list: VeiculoView[]): VeiculoView[] {
    return [...(list ?? [])].sort((a, b) => {
      const nomeA = (a.clienteNome ?? '').toLowerCase();
      const nomeB = (b.clienteNome ?? '').toLowerCase();

      const compareCliente = nomeA.localeCompare(nomeB, 'pt-BR', { sensitivity: 'base' });
      if (compareCliente !== 0) return compareCliente;

      const modeloA = (a.modelo ?? '').toLowerCase();
      const modeloB = (b.modelo ?? '').toLowerCase();
      return modeloA.localeCompare(modeloB, 'pt-BR', { sensitivity: 'base' });
    });
  }

  /** Aplica busca local sobre campos do veículo + clienteNome, mantendo paginação por aba. */
  aplicarFiltro(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    const filtrados = (list: VeiculoView[]) =>
      (list ?? []).filter((v) => {
        const placa = (v.placa ?? '').toLowerCase();
        const modelo = (v.modelo ?? '').toLowerCase();
        const marca = (v.marca ?? '').toLowerCase();
        const tipo = String(v.tipo ?? '').toLowerCase();
        const clienteNome = (v.clienteNome ?? '').toLowerCase();

        return (
          placa.includes(q) ||
          modelo.includes(q) ||
          marca.includes(q) ||
          tipo.includes(q) ||
          clienteNome.includes(q)
        );
      });

    this.veiculosAtivosFiltrados = this.ordenarVeiculos(filtrados(this.todosAtivos));
    this.veiculosInativosFiltrados = this.ordenarVeiculos(filtrados(this.todosInativos));

    this.totalAtivosElementos = this.veiculosAtivosFiltrados.length;
    this.totalInativosElementos = this.veiculosInativosFiltrados.length;

    this.ajustarPaginacaoAtivos();
    this.ajustarPaginacaoInativos();

    this.atualizarPaginaAtivos();
    this.atualizarPaginaInativos();
  }

  onSearchChange(): void {
    this.paginaIndiceAtivos = 0;
    this.paginaIndiceInativos = 0;
    this.aplicarFiltro();
  }

  onPageChangeAtivos(event: PageEvent): void {
    this.paginaIndiceAtivos = event.pageIndex;
    this.tamanhoPaginaAtivos = event.pageSize;
    this.atualizarPaginaAtivos();
  }

  onPageChangeInativos(event: PageEvent): void {
    this.paginaIndiceInativos = event.pageIndex;
    this.tamanhoPaginaInativos = event.pageSize;
    this.atualizarPaginaInativos();
  }

  private atualizarPaginaAtivos(): void {
    const inicio = this.paginaIndiceAtivos * this.tamanhoPaginaAtivos;
    const fim = inicio + this.tamanhoPaginaAtivos;
    this.veiculosAtivos.data = this.veiculosAtivosFiltrados.slice(inicio, fim);
  }

  private atualizarPaginaInativos(): void {
    const inicio = this.paginaIndiceInativos * this.tamanhoPaginaInativos;
    const fim = inicio + this.tamanhoPaginaInativos;
    this.veiculosInativos.data = this.veiculosInativosFiltrados.slice(inicio, fim);
  }

  private ajustarPaginacaoAtivos(): void {
    const totalPaginas = Math.ceil(this.totalAtivosElementos / this.tamanhoPaginaAtivos);
    const ultimoIndice = Math.max(0, totalPaginas - 1);
    if (this.paginaIndiceAtivos > ultimoIndice) this.paginaIndiceAtivos = ultimoIndice;
  }

  private ajustarPaginacaoInativos(): void {
    const totalPaginas = Math.ceil(this.totalInativosElementos / this.tamanhoPaginaInativos);
    const ultimoIndice = Math.max(0, totalPaginas - 1);
    if (this.paginaIndiceInativos > ultimoIndice) this.paginaIndiceInativos = ultimoIndice;
  }

  abrirCriar(): void {
    const ref = this.dialog.open(VeiculoFormDialogComponent, {
      width: '560px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((created: Veiculo | null) => {
      if (!created) return;
      this.snack.open('Veiculo criado com sucesso', 'Fechar', { duration: 2500 });
      this.carregar();
    });
  }

  abrirEditar(veiculo: Veiculo): void {
    const ref = this.dialog.open(VeiculoFormDialogComponent, {
      width: '560px',
      data: { mode: 'edit', veiculo },
    });

    ref.afterClosed().subscribe((updated: Veiculo | null) => {
      if (!updated) return;
      this.snack.open('Veiculo atualizado com sucesso', 'Fechar', { duration: 2500 });
      this.carregar();
    });
  }

  reativar(veiculo: Veiculo): void {
    if (!veiculo.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar veiculo',
        message: `Tem certeza que deseja reativar o veiculo "${veiculo.placa}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.veiculoService.atualizar(veiculo.id, { ativo: true }).subscribe({
        next: () => {
          this.snack.open('Veiculo reativado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message ?? 'Erro ao reativar veiculo';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarDelete(veiculo: Veiculo): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Remover veiculo',
        message: `Deseja remover o veiculo "${veiculo.placa}"? (ele sera desativado)`,
        confirmText: 'Remover',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.veiculoService.excluir(veiculo.id).subscribe({
        next: () => {
          this.snack.open('Veiculo removido (inativado)', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message ?? 'Erro ao remover veiculo';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }
}
