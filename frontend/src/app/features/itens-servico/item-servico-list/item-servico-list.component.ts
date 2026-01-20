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

import { ItemServico, ItemServicoService } from '../data/item-servico.service';
import { ItemServicoFormDialogComponent } from '../item-servico-form-dialog/item-servico-form-dialog.component';
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
  ],
  templateUrl: './item-servico-list.component.html',
  styleUrl: './item-servico-list.component.scss'
})
export class ItemServicoListComponent implements OnInit {
  private itemServicoService = inject(ItemServicoService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  displayedColumns = ['servico', 'ordem', 'quantidade', 'valor', 'acoes'];

  itensAtivos = new MatTableDataSource<ItemServico>([]);
  itensInativos = new MatTableDataSource<ItemServico>([]);
  abaSelecionada = 0;

  totalAtivos = 0;
  totalInativos = 0;

  loading = false;
  search = '';

  todosAtivos: ItemServico[] = [];
  todosInativos: ItemServico[] = [];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;

    this.itemServicoService.listar().subscribe({
      next: (list: ItemServico[]) => {
        const itens = (list ?? []) as ItemServico[];
        this.separaEOrdena(itens);
        this.aplicarFiltro();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Erro ao carregar itens de serviço', 'Fechar', { duration: 3000 });
      }
    });
  }

  private separaEOrdena(list: ItemServico[]): void {
    const ativos = list.filter(i => i.ativo);
    const inativos = list.filter(i => !i.ativo);

    this.todosAtivos = this.ordenarItens(ativos);
    this.todosInativos = this.ordenarItens(inativos);

    this.totalAtivos = this.todosAtivos.length;
    this.totalInativos = this.todosInativos.length;
  }

  private ordenarItens(list: ItemServico[]): ItemServico[] {
    return [...(list ?? [])].sort((a, b) => {
      const nomeA = (a.nomeServico ?? '').toLowerCase();
      const nomeB = (b.nomeServico ?? '').toLowerCase();
      return nomeA.localeCompare(nomeB, 'pt-BR', { sensitivity: 'base' });
    });
  }

  aplicarFiltro(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    const filtrar = (list: ItemServico[]) =>
      (list ?? []).filter((item) => {
        const nomeServico = (item.nomeServico ?? '').toLowerCase();
        const ordemId = (item.ordemServicoId ?? '').toString();
        return nomeServico.includes(q) || ordemId.includes(q);
      });

    this.itensAtivos.data = this.ordenarItens(filtrar(this.todosAtivos));
    this.itensInativos.data = this.ordenarItens(filtrar(this.todosInativos));
  }

  onSearchChange(): void {
    this.aplicarFiltro();
  }

  onTabChange(event: any): void {
    this.abaSelecionada = event.index;
  }

  abrirCriar(): void {
    const ref = this.dialog.open(ItemServicoFormDialogComponent, {
      width: '500px',
      data: { title: 'Novo Item de Serviço' }
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.itemServicoService.criar(result).subscribe({
        next: () => {
          this.snack.open('Item criado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao criar item de serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }

  abrirEditar(item: ItemServico): void {
    const ref = this.dialog.open(ItemServicoFormDialogComponent, {
      width: '500px',
      data: {
        title: `Editar Item #${item.id}`,
        initial: {
          servicoId: item.servicoId,
          quantidade: item.quantidade,
          valor: item.valorUnitario
        }
      }
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.itemServicoService.atualizar(item.id, result).subscribe({
        next: () => {
          this.snack.open('Item atualizado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao atualizar item de serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }

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
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao reativar item';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarDelete(item: ItemServico): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir item',
        message: `Deseja remover o item "${item.nomeServico}"? (ele será desativado)`,
        confirmText: 'Excluir',
        cancelText: 'Cancelar'
      }
    });

    ref.afterClosed().subscribe((confirm) => {
      if (!confirm) return;

      this.itemServicoService.deletar(item.id).subscribe({
        next: () => {
          this.snack.open('Item removido (inativado)', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao excluir item de serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }
}