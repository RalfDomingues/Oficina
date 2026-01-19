import { Component, OnInit, ViewChild } from '@angular/core';
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

import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { Veiculo } from '../../../shared/models/veiculo.model';
import { VeiculoService } from '../data/veiculo.service';
import { VeiculoFormDialogComponent } from '../veiculo-form-dialog/veiculo-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

// ✅ IMPORTA o dialog novo:
import { ClienteFiltroDialogComponent } from '../cliente-filtro-dialog/cliente-filtro-dialog.component';


// ✅ IMPORTA teu service de cliente (ajusta o path se precisar)
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
  ],
  templateUrl: './veiculo-list.component.html',
  styleUrl: './veiculo-list.component.scss',
})
export class VeiculoListComponent implements OnInit {
  displayedColumns = ['placa', 'modelo', 'marca', 'ano', 'tipo', 'clienteNome', 'ativo', 'acoes'];

  dataSource = new MatTableDataSource<VeiculoView>([]);
  totalElements = 0;

  pageIndex = 0;
  pageSize = 10;
  loading = false;

  // filtro (na página atual)
  search = '';

  // guarda a página "crua" vinda do backend (para não filtrar em cima do filtrado)
  pageRaw: VeiculoView[] = [];

  // modo de listagem
  clienteIdFiltro: number | null = null;

  // cache pra evitar buscar o mesmo cliente sempre
  private clienteNomeCache = new Map<number, string>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private veiculoService: VeiculoService,
    private clienteService: ClienteService,
    private dialog: MatDialog,
    private snack: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.carregar();
  }

  onPage(ev: PageEvent) {
    this.pageIndex = ev.pageIndex;
    this.pageSize = ev.pageSize;
    this.carregar();
  }

  carregar() {
    this.loading = true;

    const obs =
      this.clienteIdFiltro != null
        ? this.veiculoService.listarPorClientePaginado(this.clienteIdFiltro, this.pageIndex, this.pageSize)
        : this.veiculoService.listarPaginado(this.pageIndex, this.pageSize);

    obs.subscribe({
      next: (page) => {
        const raw: VeiculoView[] = (page.content ?? []) as VeiculoView[];

        this.preencherClienteNome(raw).subscribe({
          next: (enriquecidos) => {
            this.pageRaw = enriquecidos;
            this.dataSource.data = this.aplicarFiltroNaPagina(this.pageRaw);

            // mantém o total do servidor (mesmo filtrando só a página atual)
            this.totalElements = page.totalElements ?? this.pageRaw.length;

            this.loading = false;
          },
          error: () => {
            // se der ruim ao buscar nomes, ainda mostra a lista sem nome
            this.pageRaw = raw;
            this.dataSource.data = this.aplicarFiltroNaPagina(this.pageRaw);
            this.totalElements = page.totalElements ?? this.pageRaw.length;
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

    // nada pra buscar
    if (missing.length === 0) {
      return of(
        (list ?? []).map((v) => ({
          ...v,
          clienteNome: v.clienteId ? this.clienteNomeCache.get(v.clienteId) ?? null : null,
        }))
      );
    }

    // busca os clientes que faltam
    const reqs = missing.map((id) =>
      // ⚠️ AJUSTE o nome do método se o teu ClienteService for diferente
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

  aplicarFiltroNaPagina(list: VeiculoView[]): VeiculoView[] {
    const q = (this.search ?? '').trim().toLowerCase();
    if (!q) return list ?? [];

    return (list ?? []).filter((v) => {
      const placa = (v.placa ?? '').toLowerCase();
      const modelo = (v.modelo ?? '').toLowerCase();
      const marca = (v.marca ?? '').toLowerCase();
      const tipo = String(v.tipo ?? '').toLowerCase();

      const clienteId = String(v.clienteId ?? '');
      const clienteNome = (v.clienteNome ?? '').toLowerCase();

      return (
        placa.includes(q) ||
        modelo.includes(q) ||
        marca.includes(q) ||
        tipo.includes(q) ||
        clienteId.includes(q) ||
        clienteNome.includes(q)
      );
    });
  }

  onSearchChange() {
    this.dataSource.data = this.aplicarFiltroNaPagina(this.pageRaw);
  }

  limparFiltroCliente() {
    this.clienteIdFiltro = null;
    this.pageIndex = 0;
    this.carregar();
  }

  // ✅ substitui o prompt por dialog
  filtrarPorClientePrompt() {
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
    this.pageIndex = 0;
    this.carregar();
  });
}


  abrirCriar() {
    const ref = this.dialog.open(VeiculoFormDialogComponent, {
      width: '560px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((created: Veiculo | null) => {
      if (!created) return;
      this.snack.open('Veículo criado com sucesso', 'Fechar', { duration: 2500 });
      this.pageIndex = 0;
      this.carregar();
    });
  }

  abrirEditar(v: Veiculo) {
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

  confirmarDelete(v: Veiculo) {
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

          if (this.pageRaw.length <= 1 && this.pageIndex > 0) {
            this.pageIndex--;
          }

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
