import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { finalize } from 'rxjs/operators';

import { ItemServico, ItemServicoService } from '../data/item-servico.service';
import { ItemServicoFormDialogComponent, ItemServicoFormData } from '../item-servico-form-dialog/item-servico-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-item-servico-list',
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
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatPaginatorModule,
  ],
  templateUrl: './item-servico-list.component.html',
  styleUrl: './item-servico-list.component.scss',
})
export class ItemServicoListComponent implements OnInit {
  private readonly itemServicoService = inject(ItemServicoService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);

  displayedColumns = ['id', 'ordem', 'servico', 'quantidade', 'valor', 'acoes'];

  itensAtivos = new MatTableDataSource<ItemServico>([]);
  itensInativos = new MatTableDataSource<ItemServico>([]);
  abaSelecionada = 0;

  pageIndexAtivos = 0;
  pageSizeAtivos = 10;
  totalAtivosElements = 0;

  pageIndexInativos = 0;
  pageSizeInativos = 10;
  totalInativosElements = 0;

  loading = false;
  search = '';

  todosItensAtivos: ItemServico[] = [];
  todosItensInativos: ItemServico[] = [];

  itensAtivosFiltrados: ItemServico[] = [];
  itensInativosFiltrados: ItemServico[] = [];

  ngOnInit(): void {
    this.carregarTodosItens();
  }

  /**
   * Carrega todos os itens e aplica:
   * - separação ativo/inativo
   * - filtro por texto
   * - paginação client-side por aba
   */
  carregarTodosItens(): void {
    this.loading = true;

    this.itemServicoService
      .listarTodos()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (list: ItemServico[]) => {
          this.separaEOrdena(list ?? []);
          this.aplicarFiltro();
        },
        error: () => {
          this.snack.open('Erro ao carregar itens de servico', 'Fechar', { duration: 3000 });
        },
      });
  }

  private separaEOrdena(list: ItemServico[]): void {
    const ativos = (list ?? []).filter((i) => i.ativo);
    const inativos = (list ?? []).filter((i) => !i.ativo);

    this.todosItensAtivos = this.ordenarItens(ativos);
    this.todosItensInativos = this.ordenarItens(inativos);
  }

  /**
   * Ordena para exibição: OS mais recente primeiro, depois id do item.
   * Mantém a tabela “agrupada” por ordem de serviço.
   */
  private ordenarItens(list: ItemServico[]): ItemServico[] {
    return [...(list ?? [])].sort((a, b) => {
      if (b.ordemServicoId !== a.ordemServicoId) {
        return b.ordemServicoId - a.ordemServicoId;
      }
      return b.id - a.id;
    });
  }

  /**
   * Busca simples por nome do serviço e id da ordem.
   * Recalcula totais e ajusta a página para evitar índice inválido após filtro.
   */
  aplicarFiltro(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    const filtrar = (list: ItemServico[]) =>
      (list ?? []).filter((item) => {
        const nomeServico = (item.nomeServico ?? '').toLowerCase();
        const ordemId = String(item.ordemServicoId ?? '');
        return nomeServico.includes(q) || ordemId.includes(q);
      });

    this.itensAtivosFiltrados = this.ordenarItens(filtrar(this.todosItensAtivos));
    this.itensInativosFiltrados = this.ordenarItens(filtrar(this.todosItensInativos));

    this.totalAtivosElements = this.itensAtivosFiltrados.length;
    this.totalInativosElements = this.itensInativosFiltrados.length;

    this.ajustarPaginacaoAtivos();
    this.ajustarPaginacaoInativos();

    this.atualizarPaginaAtivos();
    this.atualizarPaginaInativos();
  }

  onSearchChange(): void {
    this.pageIndexAtivos = 0;
    this.pageIndexInativos = 0;
    this.aplicarFiltro();
  }

  onTabChange(indice: number): void {
    this.abaSelecionada = indice;
  }

  onPageChangeAtivos(event: PageEvent): void {
    this.pageIndexAtivos = event.pageIndex;
    this.pageSizeAtivos = event.pageSize;
    this.atualizarPaginaAtivos();
  }

  onPageChangeInativos(event: PageEvent): void {
    this.pageIndexInativos = event.pageIndex;
    this.pageSizeInativos = event.pageSize;
    this.atualizarPaginaInativos();
  }

  private atualizarPaginaAtivos(): void {
    const inicio = this.pageIndexAtivos * this.pageSizeAtivos;
    const fim = inicio + this.pageSizeAtivos;
    this.itensAtivos.data = this.itensAtivosFiltrados.slice(inicio, fim);
  }

  private atualizarPaginaInativos(): void {
    const inicio = this.pageIndexInativos * this.pageSizeInativos;
    const fim = inicio + this.pageSizeInativos;
    this.itensInativos.data = this.itensInativosFiltrados.slice(inicio, fim);
  }

  private ajustarPaginacaoAtivos(): void {
    const totalPaginas = Math.ceil(this.totalAtivosElements / this.pageSizeAtivos);
    const ultimoIndice = Math.max(0, totalPaginas - 1);
    if (this.pageIndexAtivos > ultimoIndice) this.pageIndexAtivos = ultimoIndice;
  }

  private ajustarPaginacaoInativos(): void {
    const totalPaginas = Math.ceil(this.totalInativosElements / this.pageSizeInativos);
    const ultimoIndice = Math.max(0, totalPaginas - 1);
    if (this.pageIndexInativos > ultimoIndice) this.pageIndexInativos = ultimoIndice;
  }

  /**
   * Abre o dialog e cria o item no backend.
   * Após salvar, recarrega a lista para refletir os valores calculados no back.
   */
  abrirCriar(): void {
    const data: ItemServicoFormData = { mode: 'create' };

    const ref = this.dialog.open(ItemServicoFormDialogComponent, {
      width: '500px',
      data,
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.itemServicoService.criar(result).subscribe({
        next: () => {
          this.snack.open('Item criado com sucesso', 'Fechar', { duration: 2500 });
          this.carregarTodosItens();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao criar item de servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  /**
   * Abre o dialog com dados do item e atualiza no backend.
   * Após salvar, recarrega a lista para manter a tela consistente com o back.
   */
  abrirEditar(item: ItemServico): void {
    const data: ItemServicoFormData = { mode: 'edit', item };

    const ref = this.dialog.open(ItemServicoFormDialogComponent, {
      width: '500px',
      data,
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.itemServicoService.atualizar(item.id, result).subscribe({
        next: () => {
          this.snack.open('Item atualizado com sucesso', 'Fechar', { duration: 2500 });
          this.carregarTodosItens();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao atualizar item de servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  /**
   * Reativa um item (soft toggle de ativo).
   */
  reativar(item: ItemServico): void {
    if (!item.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar item',
        message: `Tem certeza que deseja reativar o item "${item.nomeServico}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.itemServicoService.atualizar(item.id, { ativo: true }).subscribe({
        next: () => {
          this.snack.open('Item reativado com sucesso', 'Fechar', { duration: 2500 });
          this.carregarTodosItens();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao reativar item';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  /**
   * Confirma exclusão. No backend o delete pode ser soft (inativar) ou físico,
   * por isso a UI só comunica "removido/inativado" e recarrega a lista.
   */
  confirmarDelete(item: ItemServico): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir item',
        message: `Deseja remover o item "${item.nomeServico}"? (ele sera desativado)`,
        confirmText: 'Excluir',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm) => {
      if (!confirm) return;

      this.itemServicoService.deletar(item.id).subscribe({
        next: () => {
          this.snack.open('Item removido (inativado)', 'Fechar', { duration: 2500 });
          this.carregarTodosItens();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao excluir item de servico';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }
}
