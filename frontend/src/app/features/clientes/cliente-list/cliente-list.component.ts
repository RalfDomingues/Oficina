import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

import { AuthService } from '../../../core/auth/auth.service';
import { ClienteService } from '../data/cliente.service';
import { VeiculoService } from '../../veiculos/data/veiculo.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { PageResponse } from '../../../shared/models/page.model';

import { ClienteFormDialogComponent } from '../cliente-form-dialog/cliente-form-dialog.component';
import { ClienteDetailsDialogComponent } from '../cliente-details-dialog/cliente-details-dialog.component';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatIconModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './cliente-list.component.html',
  styleUrl: './cliente-list.component.scss',
})
export class ClienteListComponent implements OnInit {
  private readonly clienteService = inject(ClienteService);
  private readonly veiculoService = inject(VeiculoService);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);

  // Estado de UI
  loading = false;
  errorMsg: string | null = null;

  // Base completa vinda do backend
  clientes: Cliente[] = [];

  // DataSources da tabela (alimentados pela paginação client-side)
  clientesAtivosTabela = new MatTableDataSource<Cliente>([]);
  clientesInativosTabela = new MatTableDataSource<Cliente>([]);

  // Listas separadas por status
  todosClientesAtivos: Cliente[] = [];
  todosClientesInativos: Cliente[] = [];

  // Listas filtradas pela busca (antes de paginar)
  clientesAtivosFiltrados: Cliente[] = [];
  clientesInativosFiltrados: Cliente[] = [];

  // Paginação por aba
  paginaIndiceAtivos = 0;
  tamanhoPaginaAtivos = 10;
  totalAtivosElementos = 0;

  paginaIndiceInativos = 0;
  tamanhoPaginaInativos = 10;
  totalInativosElementos = 0;

  abaSelecionada = 0;
  search = '';
  canWrite = false;

  colunasTabela = ['nome', 'email', 'telefone', 'acoes'];

  ngOnInit(): void {
    this.canWrite = this.auth.hasAnyRole('ADMIN', 'SECRETARIA');
    this.load();
  }

  /**
   * Carrega todos os clientes e prepara:
   * - separação ativo/inativo
   * - filtro por texto
   * - paginação client-side por aba
   */
  load(): void {
    this.loading = true;
    this.errorMsg = null;

    this.clienteService
      .listarTodos()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res: Cliente[] | PageResponse<Cliente>) => {
          const list = Array.isArray(res) ? res : (res?.content ?? []);
          this.clientes = this.ordenarClientes(list);
          this.organizarListas();
          this.aplicarFiltro();
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 403) this.errorMsg = 'Voce nao tem permissao para visualizar clientes.';
          else this.errorMsg = 'Erro ao carregar clientes.';
        },
      });
  }

  private organizarListas(): void {
    this.todosClientesAtivos = this.ordenarClientes(this.clientes.filter((c) => c.ativo));
    this.todosClientesInativos = this.ordenarClientes(this.clientes.filter((c) => !c.ativo));
  }

  private ordenarClientes(lista: Cliente[]): Cliente[] {
    return [...(lista ?? [])].sort((a, b) =>
      (a?.nome ?? '').localeCompare((b?.nome ?? ''), 'pt-BR', { sensitivity: 'base' })
    );
  }

  /**
   * Busca simples em nome/email/telefone (campos opcionais podem não estar no model).
   * O termo já deve vir normalizado (trim + lowercase).
   */
  private filtrarClientes(lista: Cliente[], termo: string): Cliente[] {
    if (!termo) return lista;

    return (lista ?? []).filter((cliente) => {
      const nome = (cliente.nome ?? '').toLowerCase();
      const email = this.getClienteField(cliente, 'email').toLowerCase();
      const telefone = this.getClienteField(cliente, 'telefone').toLowerCase();
      return nome.includes(termo) || email.includes(termo) || telefone.includes(termo);
    });
  }

  /**
   * Aplica o termo de busca nas listas, recalcula totais e ajusta página atual
   * para evitar ficar em uma página inválida após filtro.
   */
  aplicarFiltro(): void {
    const termo = (this.search ?? '').trim().toLowerCase();

    this.clientesAtivosFiltrados = this.ordenarClientes(this.filtrarClientes(this.todosClientesAtivos, termo));
    this.clientesInativosFiltrados = this.ordenarClientes(this.filtrarClientes(this.todosClientesInativos, termo));

    this.totalAtivosElementos = this.clientesAtivosFiltrados.length;
    this.totalInativosElementos = this.clientesInativosFiltrados.length;

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

  onTabChange(indice: number): void {
    this.abaSelecionada = indice;
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
    this.clientesAtivosTabela.data = this.clientesAtivosFiltrados.slice(inicio, fim);
  }

  private atualizarPaginaInativos(): void {
    const inicio = this.paginaIndiceInativos * this.tamanhoPaginaInativos;
    const fim = inicio + this.tamanhoPaginaInativos;
    this.clientesInativosTabela.data = this.clientesInativosFiltrados.slice(inicio, fim);
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

  /**
   * Reativa um cliente (soft toggle de ativo).
   * Reaproveita a lista local para refletir a mudança sem recarregar tudo do back.
   */
  reativar(cliente: Cliente): void {
    if (!this.canWrite || !cliente.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar cliente',
        message: `Tem certeza que deseja reativar "${cliente.nome}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.loading = true;
      this.errorMsg = null;

      this.clienteService
        .atualizar(cliente.id!, { ativo: true })
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: () => {
            cliente.ativo = true;
            this.organizarListas();
            this.aplicarFiltro();
          },
          error: (err: HttpErrorResponse) => {
            this.errorMsg = err?.error?.message ?? 'Erro ao reativar cliente.';
          },
        });
    });
  }

  abrirNovo(): void {
    if (!this.canWrite) return;

    const ref = this.dialog.open(ClienteFormDialogComponent, {
      width: '560px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((created: Cliente | null) => {
      if (!created) return;
      this.clientes = this.ordenarClientes([created, ...this.clientes]);
      this.organizarListas();
      this.aplicarFiltro();
    });
  }

  verDetalhes(cliente: Cliente): void {
    this.dialog.open(ClienteDetailsDialogComponent, {
      width: '520px',
      data: { cliente },
    });
  }

  editar(cliente: Cliente): void {
    if (!this.canWrite) return;

    const ref = this.dialog.open(ClienteFormDialogComponent, {
      width: '560px',
      data: { mode: 'edit', cliente },
    });

    ref.afterClosed().subscribe((updated: Cliente | null) => {
      if (!updated) return;

      this.clientes = this.ordenarClientes(
        this.clientes.map((item) => (item.id === updated.id ? updated : item))
      );
      this.organizarListas();
      this.aplicarFiltro();
    });
  }

  formatTelefone(value: string | null | undefined): string {
    const digits = String(value ?? '').replace(/\D/g, '');
    if (!digits) return '-';

    if (digits.length === 11) return `${digits.slice(0, 2)} ${digits.slice(2, 7)}-${digits.slice(7)}`;
    if (digits.length === 10) return `${digits.slice(0, 2)} ${digits.slice(2, 6)}-${digits.slice(6)}`;

    return digits;
  }

  /**
   * Exclui (soft delete no back) e, por consistência, desativa veículos do cliente.
   * Observação: o fluxo desativar veículos aqui é best-effort; não bloqueia o término.
   */
  excluir(cliente: Cliente): void {
    if (!this.canWrite) return;

    const id = cliente.id;
    if (id == null) {
      this.errorMsg = 'Cliente invalido (sem ID).';
      return;
    }

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir cliente',
        message: `Tem certeza que deseja excluir "${cliente.nome}"?`,
        confirmText: 'Excluir',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.loading = true;
      this.errorMsg = null;

      this.clienteService.excluir(id).subscribe({
        next: () => {
          const atualizarCliente = () => {
            this.clientes = this.ordenarClientes(
              this.clientes.map((item) => (item.id === id ? { ...item, ativo: false } : item))
            );
            this.organizarListas();
            this.aplicarFiltro();
            this.loading = false;
          };

          // Mantém a lógica atual: se existe cliente com esse id na lista, tenta desativar veículos
          const veiculosDoCliente = this.clientes.find((cli) => cli.id === id)?.id;

          if (veiculosDoCliente) {
            this.desativarVeiculosDoCliente(id, atualizarCliente);
          } else {
            atualizarCliente();
          }
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;

          const backendMsg =
            (err.error as any)?.message ||
            (err.error as any)?.mensagem ||
            (err.error as any)?.erro ||
            null;

          if (err.status === 403) this.errorMsg = 'Voce nao tem permissao para excluir clientes.';
          else this.errorMsg = backendMsg ?? 'Erro ao excluir cliente.';
        },
      });
    });
  }

  /**
   * Desativa veículos do cliente (soft toggle de ativo).
   * Mantém o comportamento original: tenta desativar todos e chama onComplete no final,
   * mesmo se alguns falharem.
   */
  private desativarVeiculosDoCliente(clienteId: number, onComplete: () => void): void {
    this.veiculoService.listarTodos().subscribe({
      next: (veiculos) => {
        const veiculosParaDesativar = (veiculos ?? []).filter((v) => v.clienteId === clienteId && v.ativo);

        if (veiculosParaDesativar.length === 0) {
          onComplete();
          return;
        }

        let completed = 0;
        let hadError = false;

        veiculosParaDesativar.forEach((veiculo) => {
          if (!veiculo.id) {
            completed++;
            return;
          }

          this.veiculoService.atualizar(veiculo.id, { ativo: false }).subscribe({
            next: () => {
              completed++;
              if (completed === veiculosParaDesativar.length && !hadError) {
                onComplete();
              }
            },
            error: () => {
              hadError = true;
              completed++;
              if (completed === veiculosParaDesativar.length) {
                onComplete();
              }
            },
          });
        });
      },
      error: () => {
        onComplete();
      },
    });
  }

  private getClienteField(cliente: Cliente, key: 'email' | 'telefone'): string {
    return String((cliente as any)?.[key] ?? '');
  }
}
