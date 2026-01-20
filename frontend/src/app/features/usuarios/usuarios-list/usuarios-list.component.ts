import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs/operators';

import { Usuario } from '../../../shared/models/usuario.model';
import { UsuarioService } from '../data/usuario.service';
import { UsuarioFormDialogComponent } from '../usuario-form-dialog/usuario-form-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-usuarios-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatCardModule,
    MatTabsModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './usuarios-list.component.html',
  styleUrl: './usuarios-list.component.scss',
})
export class UsuariosListComponent implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);

  displayedColumns = ['nome', 'email', 'perfil', 'acoes'];

  usuariosAtivos = new MatTableDataSource<Usuario>([]);
  usuariosInativos = new MatTableDataSource<Usuario>([]);

  pageIndexAtivos = 0;
  pageSizeAtivos = 10;
  totalAtivosElements = 0;
  loadingAtivos = false;

  pageIndexInativos = 0;
  pageSizeInativos = 10;
  totalInativosElements = 0;
  loadingInativos = false;

  abaSelecionada = 0;

  todosUsuariosAtivos: Usuario[] = [];
  todosUsuariosInativos: Usuario[] = [];

  /**
   * Tela de usuários com abas (ativos/inativos) e paginação client-side.
   * As ações alteram o estado local para refletir rapidamente na UI após o backend responder.
   */
  ngOnInit(): void {
    this.carregarTodosUsuarios();
  }

  private carregarTodosUsuarios(): void {
    this.loadingAtivos = true;
    this.loadingInativos = true;

    this.usuarioService
      .listarTodos()
      .pipe(
        finalize(() => {
          this.loadingAtivos = false;
          this.loadingInativos = false;
        })
      )
      .subscribe({
        next: (lista) => {
          this.separarUsuarios((lista ?? []) as Usuario[]);
          this.atualizarPaginaAtivos();
          this.atualizarPaginaInativos();
        },
        error: () => {
          this.snack.open('Erro ao carregar usuarios', 'Fechar', { duration: 3000 });
        },
      });
  }

  private separarUsuarios(lista: Usuario[]): void {
    const ativos = (lista ?? []).filter((usuario) => usuario.ativo);
    const inativos = (lista ?? []).filter((usuario) => !usuario.ativo);

    this.todosUsuariosAtivos = this.ordenarUsuarios(ativos);
    this.todosUsuariosInativos = this.ordenarUsuarios(inativos);

    this.totalAtivosElements = this.todosUsuariosAtivos.length;
    this.totalInativosElements = this.todosUsuariosInativos.length;

    this.ajustarPaginacaoAtivos();
    this.ajustarPaginacaoInativos();
  }

  /** Ordena por nome para manter a lista estável entre recarregamentos e mudanças de aba. */
  private ordenarUsuarios(lista: Usuario[]): Usuario[] {
    return [...(lista ?? [])].sort((a, b) => {
      const nomeA = (a.nome ?? '').toLowerCase();
      const nomeB = (b.nome ?? '').toLowerCase();
      return nomeA.localeCompare(nomeB, 'pt-BR', { sensitivity: 'base' });
    });
  }

  private atualizarPaginaAtivos(): void {
    const inicio = this.pageIndexAtivos * this.pageSizeAtivos;
    const fim = inicio + this.pageSizeAtivos;
    this.usuariosAtivos.data = this.todosUsuariosAtivos.slice(inicio, fim);
  }

  private atualizarPaginaInativos(): void {
    const inicio = this.pageIndexInativos * this.pageSizeInativos;
    const fim = inicio + this.pageSizeInativos;
    this.usuariosInativos.data = this.todosUsuariosInativos.slice(inicio, fim);
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

  onTabChange(indice: number): void {
    this.abaSelecionada = indice;
    if (indice === 0) this.atualizarPaginaAtivos();
    if (indice === 1) this.atualizarPaginaInativos();
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
          this.snack.open('Usuario criado com sucesso', 'Fechar', { duration: 2500 });
          this.pageIndexAtivos = 0;
          this.carregarTodosUsuarios();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao criar usuario';
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
          this.snack.open('Usuario atualizado com sucesso', 'Fechar', { duration: 2500 });
          this.carregarTodosUsuarios();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao atualizar usuario';
          this.snack.open(mensagem, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarDesativar(usuario: Usuario): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Desativar usuario',
        message: `Deseja desativar o usuario "${usuario.nome}"?`,
        confirmText: 'Desativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirmar) => {
      if (!confirmar) return;

      this.usuarioService.desativar(usuario.id).subscribe({
        next: () => {
          this.snack.open('Usuario desativado', 'Fechar', { duration: 2500 });

          const usuariosAtivosAtualizados = this.todosUsuariosAtivos.filter((u) => u.id !== usuario.id);
          const usuarioInativado = { ...usuario, ativo: false };

          this.todosUsuariosAtivos = this.ordenarUsuarios(usuariosAtivosAtualizados);
          this.todosUsuariosInativos = this.ordenarUsuarios([usuarioInativado, ...this.todosUsuariosInativos]);

          this.totalAtivosElements = this.todosUsuariosAtivos.length;
          this.totalInativosElements = this.todosUsuariosInativos.length;

          this.ajustarPaginacaoAtivos();
          this.atualizarPaginaAtivos();

          if (this.abaSelecionada === 1) this.atualizarPaginaInativos();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao desativar usuario';
          this.snack.open(mensagem, 'Fechar', { duration: 3500 });
        },
      });
    });
  }

  confirmarReativar(usuario: Usuario): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar usuario',
        message: `Deseja reativar o usuario "${usuario.nome}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirmar) => {
      if (!confirmar) return;

      this.usuarioService.reativar(usuario.id).subscribe({
        next: () => {
          this.snack.open('Usuario reativado com sucesso', 'Fechar', { duration: 2500 });

          const usuariosInativosAtualizados = this.todosUsuariosInativos.filter((u) => u.id !== usuario.id);
          const usuarioReativado = { ...usuario, ativo: true };

          this.todosUsuariosInativos = this.ordenarUsuarios(usuariosInativosAtualizados);
          this.todosUsuariosAtivos = this.ordenarUsuarios([usuarioReativado, ...this.todosUsuariosAtivos]);

          this.totalAtivosElements = this.todosUsuariosAtivos.length;
          this.totalInativosElements = this.todosUsuariosInativos.length;

          this.ajustarPaginacaoInativos();
          this.atualizarPaginaInativos();

          if (this.abaSelecionada === 0) this.atualizarPaginaAtivos();
        },
        error: (erro: unknown) => {
          const mensagem = (erro as any)?.error?.message ?? 'Erro ao reativar usuario';
          this.snack.open(mensagem, 'Fechar', { duration: 3500 });
        },
      });
    });
  }
}
