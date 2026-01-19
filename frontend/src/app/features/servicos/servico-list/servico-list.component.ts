import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { Servico } from '../../../shared/models/servico.model';
import { ServicoService } from '../data/servico.service';
import { ServicoFormDialogComponent } from '../servico-form-dialog/servico-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-servico-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatPaginatorModule,
    MatSnackBarModule
  ],
  templateUrl: './servico-list.component.html',
  styleUrl: './servico-list.component.scss'
})
export class ServicoListComponent implements OnInit {
  displayedColumns = ['nome', 'preco', 'ativo', 'acoes'];

  dataSource = new MatTableDataSource<Servico>([]);
  totalElements = 0;

  pageIndex = 0;
  pageSize = 10;

  loading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private servicoService: ServicoService,
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

    this.servicoService.listar(this.pageIndex, this.pageSize).subscribe({
      next: (page) => {
        this.dataSource.data = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Erro ao carregar serviços', 'Fechar', { duration: 3000 });
      }
    });
  }

  abrirCriar() {
    const ref = this.dialog.open(ServicoFormDialogComponent, {
      width: '420px',
      data: { title: 'Novo Serviço' }
    });

    ref.afterClosed().subscribe((result) => {
      if (!result) return;

      this.servicoService.criar(result).subscribe({
        next: () => {
          this.snack.open('Serviço criado com sucesso', 'Fechar', { duration: 2500 });
          this.pageIndex = 0;
          this.carregar();
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Erro ao criar serviço';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        }
      });
    });
  }

  abrirEditar(servico: Servico) {
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

  confirmarDelete(servico: Servico) {
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

          // se apagou o último item da página, volta 1 página
          if (this.dataSource.data.length === 1 && this.pageIndex > 0) {
            this.pageIndex--;
          }
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
