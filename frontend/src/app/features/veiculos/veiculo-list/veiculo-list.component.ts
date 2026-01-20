// veiculo-list.component.ts (COMPLETO)
import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { Veiculo } from '../../../shared/models/veiculo.model';
import { VeiculoService } from '../data/veiculo.service';
import { VeiculoFormDialogComponent } from '../veiculo-form-dialog/veiculo-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ClienteFiltroDialogComponent } from '../cliente-filtro-dialog/cliente-filtro-dialog.component';
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
  ],
  templateUrl: './veiculo-list.component.html',
  styleUrl: './veiculo-list.component.scss',
})
export class VeiculoListComponent implements OnInit {
  private veiculoService = inject(VeiculoService);
  private clienteService = inject(ClienteService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  displayedColumns = ['placa', 'modelo', 'marca', 'ano', 'tipo', 'clienteNome', 'acoes'];

  veiculosAtivos = new MatTableDataSource<VeiculoView>([]);
  veiculosInativos = new MatTableDataSource<VeiculoView>([]);
  abaSelecionada = 0;

  totalAtivos = 0;
  totalInativos = 0;

  loading = false;
  search = '';

  // dados brutos antes de filtrar
  todosAtivos: VeiculoView[] = [];
  todosInativos: VeiculoView[] = [];

  clienteIdFiltro: number | null = null;
  private clienteNomeCache = new Map<number, string>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;

    const obs = this.clienteIdFiltro != null
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
        this.snack.open('Erro ao carregar veículos', 'Fechar', { duration: 3000 });
      },
    });
  }

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

  private separaEOrdena(list: VeiculoView[]): void {
    const ativos = list.filter(v => v.ativo);
    const inativos = list.filter(v => !v.ativo);

    this.todosAtivos = this.ordenarVeiculos(ativos);
    this.todosInativos = this.ordenarVeiculos(inativos);

    this.totalAtivos = this.todosAtivos.length;
    this.totalInativos = this.todosInativos.length;
  }

  private ordenarVeiculos(list: VeiculoView[]): VeiculoView[] {
    return [...(list ?? [])].sort((a, b) => {
      const nomeA = (a.clienteNome ?? '').toLowerCase();
      const nomeB = (b.clienteNome ?? '').toLowerCase();

      // Primeiro ordena por nome do cliente
      const compareCliente = nomeA.localeCompare(nomeB, 'pt-BR', { sensitivity: 'base' });
      if (compareCliente !== 0) return compareCliente;

      // Se mesmo cliente, ordena por modelo
      const modeloA = (a.modelo ?? '').toLowerCase();
      const modeloB = (b.modelo ?? '').toLowerCase();
      return modeloA.localeCompare(modeloB, 'pt-BR', { sensitivity: 'base' });
    });
  }

  aplicarFiltro(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    if (!q) {
      this.veiculosAtivos.data = [...this.todosAtivos];
      this.veiculosInativos.data = [...this.todosInativos];
      return;
    }

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

    this.veiculosAtivos.data = this.ordenarVeiculos(filtrados(this.todosAtivos));
    this.veiculosInativos.data = this.ordenarVeiculos(filtrados(this.todosInativos));
  }

  onSearchChange(): void {
    this.aplicarFiltro();
  }

  filtrarPorClientePrompt(): void {
    const ref = this.dialog.open(ClienteFiltroDialogComponent, {
      width: '520px',
      data: { initialId: this.clienteIdFiltro },
    });

    ref.afterClosed().subscribe((result: { clienteId: number } | null) => {
      if (!result) return;

      const id = Number(result.clienteId);
      if (!Number.isFinite(id) || id <= 0) {
        this.snack.open('Cliente inválido.', 'Fechar', { duration: 2500 });
        return;
      }

      this.clienteIdFiltro = id;
      this.carregar();
    });
  }

  limparFiltroCliente(): void {
    this.clienteIdFiltro = null;
    this.carregar();
  }

  abrirCriar(): void {
    const ref = this.dialog.open(VeiculoFormDialogComponent, {
      width: '560px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((created: Veiculo | null) => {
      if (!created) return;
      this.snack.open('Veículo criado com sucesso', 'Fechar', { duration: 2500 });
      this.carregar();
    });
  }

  abrirEditar(v: Veiculo): void {
    const ref = this.dialog.open(VeiculoFormDialogComponent, {
      width: '560px',
      data: { mode: 'edit', veiculo: v },
    });

    ref.afterClosed().subscribe((updated: Veiculo | null) => {
      if (!updated) return;
      this.snack.open('Veículo atualizado com sucesso', 'Fechar', { duration: 2500 });
      this.carregar();
    });
  }

  reativar(v: Veiculo): void {
    if (!v.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar veículo',
        message: `Tem certeza que deseja reativar o veículo "${v.placa}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.veiculoService.atualizar(v.id, { ativo: true }).subscribe({
        next: () => {
          this.snack.open('Veículo reativado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message ?? 'Erro ao reativar veículo';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarDelete(v: Veiculo): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Remover veículo',
        message: `Deseja remover o veículo "${v.placa}"? (ele será desativado)`,
        confirmText: 'Remover',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.veiculoService.excluir(v.id).subscribe({
        next: () => {
          this.snack.open('Veículo removido (inativado)', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message ?? 'Erro ao remover veículo';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }
}