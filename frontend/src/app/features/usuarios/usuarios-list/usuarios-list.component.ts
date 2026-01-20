import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';

import { Usuario } from '../../../shared/models/usuario.model';
import { UsuarioService } from '../data/usuario.service';
import { UsuarioFormDialogComponent } from '../usuario-form-dialog/usuario-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-usuarios-list',
  standalone: true,
  imports: [
  CommonModule,
  MatTableModule,
  MatButtonModule,
  MatIconModule,
  MatDialogModule,
  MatPaginatorModule,
  MatSnackBarModule,
  MatCardModule,  // ← Adicionar
],
  templateUrl: './usuarios-list.component.html',
  styleUrl: './usuarios-list.component.scss',
})
export class UsuariosListComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  displayedColumns = ['nome', 'email', 'perfil', 'ativo', 'acoes'];

  dataSource = new MatTableDataSource<Usuario>([]);
  totalElements = 0;

  pageIndex = 0;
  pageSize = 10;
  loading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngOnInit(): void {
    this.carregar();
  }

  onPage(evento: PageEvent) {
    this.pageIndex = evento.pageIndex;
    this.pageSize = evento.pageSize;
    this.carregar();
  }

  carregar(): void {
    this.loading = true;

    this.usuarioService.listar(this.pageIndex, this.pageSize).subscribe({
      next: (page) => {
        this.dataSource.data = page.content ?? [];
        this.totalElements = page.totalElements ?? 0;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snack.open('Erro ao carregar usuários', 'Fechar', { duration: 3000 });
      },
    });
  }

  abrirCriar(): void {
    const ref = this.dialog.open(UsuarioFormDialogComponent, {
      width: '520px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((resultado) => {
      if (!resultado) return;

      this.usuarioService.criar(resultado).subscribe({
        next: () => {
          this.snack.open('Usuário criado com sucesso', 'Fechar', { duration: 2500 });
          this.pageIndex = 0;
          this.carregar();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao criar usuário';
          this.snack.open(mensagem, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  abrirEditar(usuario: Usuario): void {
    const ref = this.dialog.open(UsuarioFormDialogComponent, {
      width: '520px',
      data: { mode: 'edit', usuario },
    });

    ref.afterClosed().subscribe((resultado) => {
      if (!resultado) return;

      this.usuarioService.atualizar(usuario.id, resultado).subscribe({
        next: () => {
          this.snack.open('Usuário atualizado com sucesso', 'Fechar', { duration: 2500 });
          this.carregar();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao atualizar usuário';
          this.snack.open(mensagem, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarDelete(usuario: Usuario): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Desativar usuário',
        message: `Deseja desativar o usuário "${usuario.nome}"?`,
        confirmText: 'Desativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirmar) => {
      if (!confirmar) return;

      this.usuarioService.desativar(usuario.id).subscribe({
        next: () => {
          this.snack.open('Usuário desativado', 'Fechar', { duration: 2500 });
          if (this.dataSource.data.length === 1 && this.pageIndex > 0) {
            this.pageIndex--;
          }
          this.carregar();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao desativar usuário';
          this.snack.open(mensagem, 'Fechar', { duration: 3500 });
        },
      });
    });
  }
}
