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

import { Servico } from '../../../shared/models/servico.model';
import { ServicoService } from '../data/servico.service';
import { ServicoFormDialogComponent } from '../servico-form-dialog/servico-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

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
  ],
  templateUrl: './servico-list.component.html',
  styleUrl: './servico-list.component.scss'
})
export class ServicoListComponent implements OnInit {
  private servicoService = inject(ServicoService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  displayedColumns = ['nome', 'preco', 'acoes'];

  servicosAtivos = new MatTableDataSource<Servico>([]);
  servicosInativos = new MatTableDataSource<Servico>([]);
  abaSelecionada = 0;

  totalAtivos = 0;
  totalInativos = 0;

  loading = false;
  search = '';

  // dados brutos antes de filtrar
  todosAtivos: Servico[] = [];
  todosInativos: Servico[] = [];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;

    this.servicoService.listarTodos().subscribe({
      next: (list) => {
        const servicos = (list ?? []) as Servico[];
        this.separaEOrdena(servicos);
        this.aplicarFiltro();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Erro ao carregar serviços', 'Fechar', { duration: 3000 });
      }
    });
  }

  private separaEOrdena(list: Servico[]): void {
    const ativos = list.filter(s => s.ativo);
    const inativos = list.filter(s => !s.ativo);

    this.todosAtivos = this.ordenarServiços(ativos);
    this.todosInativos = this.ordenarServiços(inativos);

    this.totalAtivos = this.todosAtivos.length;
    this.totalInativos = this.todosInativos.length;
  }

  private ordenarServiços(list: Servico[]): Servico[] {
    return [...(list ?? [])].sort((a, b) => {
      const nomeA = (a.nome ?? '').toLowerCase();
      const nomeB = (b.nome ?? '').toLowerCase();
      return nomeA.localeCompare(nomeB, 'pt-BR', { sensitivity: 'base' });
    });
  }

  aplicarFiltro(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    if (!q) {
      this.servicosAtivos.data = [...this.todosAtivos];
      this.servicosInativos.data = [...this.todosInativos];
      return;
    }

    const filtrados = (list: Servico[]) =>
      (list ?? []).filter((s) => {
        const nome = (s.nome ?? '').toLowerCase();
        return nome.includes(q);
      });

    this.servicosAtivos.data = this.ordenarServiços(filtrados(this.todosAtivos));
    this.servicosInativos.data = this.ordenarServiços(filtrados(this.todosInativos));
  }

  onSearchChange(): void {
    this.aplicarFiltro();
  }

  abrirCriar(): void {
    const ref = this.dialog.open(ServicoFormDialogComponent, {
      width: '420px',
      data: { title: 'Novo Serviço' }
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.servicoService.criar(result).subscribe({
        next: () => {
          this.snack.open('Serviço criado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao criar serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }

  abrirEditar(servico: Servico): void {
    const ref = this.dialog.open(ServicoFormDialogComponent, {
      width: '420px',
      data: {
        title: `Editar Serviço #${servico.id}`,
        initial: { nome: servico.nome, preco: servico.preco }
      }
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.servicoService.atualizar(servico.id, result).subscribe({
        next: () => {
          this.snack.open('Serviço atualizado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao atualizar serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }

  reativar(servico: Servico): void {
    if (!servico.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar serviço',
        message: `Tem certeza que deseja reativar o serviço "${servico.nome}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirm: boolean) => {
      if (!confirm) return;

      this.servicoService.atualizar(servico.id, { ativo: true }).subscribe({
        next: () => {
          this.snack.open('Serviço reativado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err: HttpErrorResponse) => {
          const msg = (err.error as any)?.message ?? 'Erro ao reativar serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarDelete(servico: Servico): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir serviço',
        message: `Deseja remover o serviço "${servico.nome}"? (ele será desativado)`,
        confirmText: 'Excluir',
        cancelText: 'Cancelar'
      }
    });

    ref.afterClosed().subscribe((confirm) => {
      if (!confirm) return;

      this.servicoService.deletar(servico.id).subscribe({
        next: () => {
          this.snack.open('Serviço removido (inativado)', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao excluir serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }
}