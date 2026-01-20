// cliente-list.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../../../core/auth/auth.service';
import { ClienteService } from '../data/cliente.service';
import { VeiculoService } from '../../veiculos/data/veiculo.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { PageResponse } from '../../../shared/models/page.model';

import { ClienteFormDialogComponent } from '../cliente-form-dialog/cliente-form-dialog.component';
import { ClienteDetailsDialogComponent } from '../cliente-details-dialog/cliente-details-dialog.component';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';

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
  ],
  templateUrl: './cliente-list.component.html',
  styleUrl: './cliente-list.component.scss',
})
export class ClienteListComponent implements OnInit {
  private clienteService = inject(ClienteService);
  private veiculoService = inject(VeiculoService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);

  loading = false;
  errorMsg: string | null = null;

  clientes: Cliente[] = [];
  filtered: Cliente[] = [];
  clientesAtivos: Cliente[] = [];
  clientesInativos: Cliente[] = [];
  abaSelecionada = 0;

  search = '';
  canWrite = false;

  ngOnInit(): void {
    this.canWrite = this.auth.hasAnyRole('ADMIN', 'SECRETARIA');
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMsg = null;

    this.clienteService.listarTodos().subscribe({
      next: (res: Cliente[] | PageResponse<Cliente>) => {
        const list = Array.isArray(res) ? res : (res?.content ?? []);
        this.clientes = this.sortByNomeComStatus(list);
        this.separatePorStatus();
        this.applyFilter();
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        if (err.status === 403) this.errorMsg = 'Você não tem permissão para visualizar clientes.';
        else this.errorMsg = 'Erro ao carregar clientes.';
      },
    });
  }

  private separatePorStatus(): void {
    this.clientesAtivos = this.clientes.filter(c => c.ativo);
    this.clientesInativos = this.clientes.filter(c => !c.ativo);
  }

  applyFilter(): void {
    const q = (this.search ?? '').trim().toLowerCase();
    const tabAtiva = this.abaSelecionada === 0 ? this.clientesAtivos : this.clientesInativos;

    if (!q) {
      if (this.abaSelecionada === 0) {
        this.clientesAtivos = [...tabAtiva];
      } else {
        this.clientesInativos = [...tabAtiva];
      }
      return;
    }

    const filtrados = tabAtiva.filter((c) => {
      const nome = (c.nome ?? '').toLowerCase();
      const email = ((c as any).email ?? '').toLowerCase();
      const telefone = ((c as any).telefone ?? '').toLowerCase();
      return nome.includes(q) || email.includes(q) || telefone.includes(q);
    });

    if (this.abaSelecionada === 0) {
      this.clientesAtivos = this.sortByNomeComStatus(filtrados);
    } else {
      this.clientesInativos = this.sortByNomeComStatus(filtrados);
    }
  }

  private sortByNomeComStatus(list: Cliente[]): Cliente[] {
    return [...(list ?? [])].sort((a, b) => {
      // Primeiro: Ativos no topo
      if (a.ativo !== b.ativo) {
        return a.ativo ? -1 : 1;  // ativos primeiro
      }

      // Dentro de cada grupo: ordem alfabética
      return (a?.nome ?? '').localeCompare((b?.nome ?? ''), 'pt-BR', { sensitivity: 'base' });
    });
  }

  reativar(c: Cliente): void {
    if (!this.canWrite || !c.id) return;

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Reativar cliente',
        message: `Tem certeza que deseja reativar "${c.nome}"?`,
        confirmText: 'Reativar',
        cancelText: 'Cancelar',
      },
    });

    ref.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.loading = true;
      this.errorMsg = null;

      this.clienteService.atualizar(c.id, { ativo: true }).subscribe({
        next: () => {
          c.ativo = true;
          this.clientes = this.sortByNomeComStatus(this.clientes);
          this.separatePorStatus();
          this.applyFilter();
          this.loading = false;
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;
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
      this.clientes = this.sortByNomeComStatus([created, ...this.clientes]);
      this.separatePorStatus();
      this.applyFilter();
    });
  }

  verDetalhes(c: Cliente): void {
    this.dialog.open(ClienteDetailsDialogComponent, {
      width: '520px',
      data: { cliente: c },
    });
  }

  editar(c: Cliente): void {
    if (!this.canWrite) return;

    const ref = this.dialog.open(ClienteFormDialogComponent, {
      width: '560px',
      data: { mode: 'edit', cliente: c },
    });

    ref.afterClosed().subscribe((updated: Cliente | null) => {
      if (!updated) return;

      this.clientes = this.sortByNomeComStatus(
        this.clientes.map((x) => (x.id === updated.id ? updated : x))
      );
      this.separatePorStatus();
      this.applyFilter();
    });
  }

  formatTelefone(value: string | null | undefined): string {
    const digits = String(value ?? '').replace(/\D/g, '');
    if (!digits) return '-';

    if (digits.length === 11) {
      return `${digits.slice(0, 2)} ${digits.slice(2, 7)}-${digits.slice(7)}`;
    }

    if (digits.length === 10) {
      return `${digits.slice(0, 2)} ${digits.slice(2, 6)}-${digits.slice(6)}`;
    }

    return digits;
  }

  excluir(c: Cliente): void {
    if (!this.canWrite) return;

    const id = c.id;
    if (id == null) {
      this.errorMsg = 'Cliente inválido (sem ID).';
      return;
    }

    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir cliente',
        message: `Tem certeza que deseja excluir "${c.nome}"?`,
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
          // Buscar todos os carros do cliente para desativar
          const veiculosDoCliente = this.clientes
            .find(cli => cli.id === id)
            ?.id;

          if (veiculosDoCliente) {
            this.desativarVeiculosDoCliente(id, () => {
              // Após desativar carros, remove cliente da lista
              this.clientes = this.sortByNomeComStatus(this.clientes.filter((x) => x.id !== id));
              this.separatePorStatus();
              this.applyFilter();
              this.loading = false;
            });
          } else {
            this.clientes = this.sortByNomeComStatus(this.clientes.filter((x) => x.id !== id));
            this.separatePorStatus();
            this.applyFilter();
            this.loading = false;
          }
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;

          const backendMsg =
            (err.error as any)?.message ||
            (err.error as any)?.mensagem ||
            (err.error as any)?.erro ||
            null;

          if (err.status === 403) this.errorMsg = 'Você não tem permissão para excluir clientes.';
          else this.errorMsg = backendMsg ?? 'Erro ao excluir cliente.';
        },
      });
    });
  }

  /**
   * Desativa todos os veículos pertencentes a um cliente.
   * Busca todos os veículos do cliente e faz update para ativo=false
   */
  private desativarVeiculosDoCliente(clienteId: number, onComplete: () => void): void {
    this.veiculoService.listarTodos().subscribe({
      next: (veiculos) => {
        // Filtrar apenas veículos do cliente que está sendo excluído
        const veiculosParaDesativar = (veiculos ?? []).filter(v => v.clienteId === clienteId && v.ativo);

        if (veiculosParaDesativar.length === 0) {
          onComplete();
          return;
        }

        // Fazer update paralelo de todos os veículos
        let completed = 0;
        let hadError = false;

        veiculosParaDesativar.forEach(veiculo => {
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
              // Continua mesmo com erro em um veículo
              if (completed === veiculosParaDesativar.length) {
                onComplete();
              }
            },
          });
        });
      },
      error: () => {
        // Se falhar ao buscar veículos, continua mesmo assim
        onComplete();
      },
    });
  }
}