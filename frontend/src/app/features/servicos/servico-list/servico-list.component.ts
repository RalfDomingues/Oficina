import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs/operators';

import { Servico } from '../../../shared/models/servico.model';
import { ServicoService } from '../data/servico.service';
import { ServicoFormDialogComponent } from '../servico-form-dialog/servico-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-servico-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatCardModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './servico-list.component.html',
  styleUrl: './servico-list.component.scss',
})
export class ServicoListComponent implements OnInit {
  private readonly servicoService = inject(ServicoService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);
  private readonly authService = inject(AuthService);

  displayedColumns: string[] = [];

  servicosAtivos = new MatTableDataSource<Servico>([]);
  servicosInativos = new MatTableDataSource<Servico>([]);
  abaSelecionada = 0;

  totalAtivosElementos = 0;
  totalInativosElementos = 0;

  paginaIndiceAtivos = 0;
  tamanhoPaginaAtivos = 10;

  paginaIndiceInativos = 0;
  tamanhoPaginaInativos = 10;

  loading = false;
  search = '';

  todosAtivos: Servico[] = [];
  todosInativos: Servico[] = [];

  servicosAtivosFiltrados: Servico[] = [];
  servicosInativosFiltrados: Servico[] = [];

  podeGerenciarServicos = false;

  /**
   * Tela de serviços com:
   * - separação ativo/inativo
   * - filtro por nome
   * - paginação client-side
   * Ações de criação/edição/reativação/exclusão só aparecem para perfis autorizados.
   */
  ngOnInit(): void {
    this.podeGerenciarServicos = this.authService.hasAnyRole('ADMIN', 'SECRETARIA');
    this.displayedColumns = this.podeGerenciarServicos ? ['nome', 'preco', 'acoes'] : ['nome', 'preco'];
    this.carregar();
  }

  carregar(): void {
    this.loading = true;

    this.servicoService
      .listarTodos()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (list) => {
          this.separaEOrdena((list ?? []) as Servico[]);
          this.aplicarFiltro();
        },
        error: () => {
          this.snack.open('Erro ao carregar servicos', 'Fechar', { duration: 3000 });
        },
      });
  }

  private separaEOrdena(list: Servico[]): void {
    const ativos = (list ?? []).filter((s) => s.ativo);
    const inativos = (list ?? []).filter((s) => !s.ativo);

    this.todosAtivos = this.ordenarServicos(ativos);
    this.todosInativos = this.ordenarServicos(inativos);
  }

  /** Ordena por nome para manter a lista estável nas abas e após filtro. */
  private ordenarServicos(list: Servico[]): Servico[] {
    return [...(list ?? [])].sort((a, b) => {
      const nomeA = (a.nome ?? '').toLowerCase();
      const nomeB = (b.nome ?? '').toLowerCase();
      return nomeA.localeCompare(nomeB, 'pt-BR', { sensitivity: 'base' });
    });
  }

  aplicarFiltro(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    const filtrados = (list: Servico[]) =>
      (list ?? []).filter((s) => (s.nome ?? '').toLowerCase().includes(q));

    this.servicosAtivosFiltrados = this.ordenarServicos(filtrados(this.todosAtivos));
    this.servicosInativosFiltrados = this.ordenarServicos(filtrados(this.todosInativos));

    this.totalAtivosElementos = this.servicosAtivosFiltrados.length;
    this.totalInativosElementos = this.servicosInativosFiltrados.length;

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
    this.servicosAtivos.data = this.servicosAtivosFiltrados.slice(inicio, fim);
  }

  private atualizarPaginaInativos(): void {
    const inicio = this.paginaIndiceInativos * this.tamanhoPaginaInativos;
    const fim = inicio + this.tamanhoPaginaInativos;
    this.servicosInativos.data = this.servicosInativosFiltrados.slice(inicio, fim);
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
    const ref = this.dialog.open(ServicoFormDialogComponent, {
      width: '420px',
      data: { title: 'Novo Servico' },
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.servicoService.criar(result).subscribe({
        next: () => {
          this.snack.open('Servico criado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao criar servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  abrirEditar(servico: Servico): void {
    const ref = this.dialog.open(ServicoFormDialogComponent, {
      width: '420px',
      data: {
        title: `Editar Servico #${servico.id}`,
        initial: { nome: servico.nome, preco: servico.preco },
      },
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.servicoService.atualizar(servico.id, result).subscribe({
        next: () => {
          this.snack.open('Servico atualizado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao atualizar servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  reativar(servico: Servico): void {
    if (!servico.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar servico',
        message: `Tem certeza que deseja reativar o servico "${servico.nome}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.servicoService.atualizar(servico.id, { ativo: true }).subscribe({
        next: () => {
          this.snack.open('Servico reativado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message ?? 'Erro ao reativar servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  /**
   * Confirma remoção. A tela trata como “inativado”, mas o comportamento real
   * depende do backend (delete físico ou soft delete).
   */
  confirmarDelete(servico: Servico): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir servico',
        message: `Deseja remover o servico "${servico.nome}"? (ele sera desativado)`,
        confirmText: 'Excluir',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm) => {
      if (!confirm) return;

      this.servicoService.deletar(servico.id).subscribe({
        next: () => {
          this.snack.open('Servico removido (inativado)', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao excluir servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }
}
