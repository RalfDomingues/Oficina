import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../../../core/auth/auth.service';
import { ClienteService } from '../data/cliente.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { PageResponse } from '../../../shared/models/page.model';

import { ClienteFormDialogComponent } from '../cliente-form-dialog/cliente-form-dialog.component';
import { ClienteDetailsDialogComponent } from '../cliente-details-dialog/cliente-details-dialog.component';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  templateUrl: './cliente-list.component.html',
  styleUrl: './cliente-list.component.scss',
})
export class ClienteListComponent implements OnInit {
  private clienteService = inject(ClienteService);
  private auth = inject(AuthService);
  private dialog = inject(MatDialog);

  loading = false;
  errorMsg: string | null = null;

  clientes: Cliente[] = [];
  filtered: Cliente[] = [];

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
        this.clientes = this.sortByNome(list);
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

  applyFilter(): void {
    const q = (this.search ?? '').trim().toLowerCase();

    if (!q) {
      this.filtered = [...this.clientes];
      return;
    }

    const filtrados = this.clientes.filter((c) => {
      const nome = (c.nome ?? '').toLowerCase();
      const email = ((c as any).email ?? '').toLowerCase();
      const telefone = ((c as any).telefone ?? '').toLowerCase();
      return nome.includes(q) || email.includes(q) || telefone.includes(q);
    });

    this.filtered = this.sortByNome(filtrados);
  }

  private sortByNome(list: Cliente[]): Cliente[] {
    return [...(list ?? [])].sort((a, b) =>
      (a?.nome ?? '').localeCompare((b?.nome ?? ''), 'pt-BR', { sensitivity: 'base' })
    );
  }

  abrirNovo(): void {
    if (!this.canWrite) return;

    const ref = this.dialog.open(ClienteFormDialogComponent, {
      width: '560px',
      data: { mode: 'create' },
    });

    ref.afterClosed().subscribe((created: Cliente | null) => {
      if (!created) return;
      this.clientes = this.sortByNome([created, ...this.clientes]);
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

      this.clientes = this.sortByNome(
        this.clientes.map((x) => (x.id === updated.id ? updated : x))
      );
      this.applyFilter();
    });
  }

  formatTelefone(value: string | null | undefined): string {
    const digits = String(value ?? '').replace(/\D/g, '');
    if (!digits) return '-';

    // Aceita com ou sem DDD
    // 11 dígitos: DDD + 9 dígitos (celular) -> 47 99612-3499
    if (digits.length === 11) {
      return `${digits.slice(0, 2)} ${digits.slice(2, 7)}-${digits.slice(7)}`;
    }

    // 10 dígitos: DDD + 8 dígitos (fixo) -> 47 3612-3499
    if (digits.length === 10) {
      return `${digits.slice(0, 2)} ${digits.slice(2, 6)}-${digits.slice(6)}`;
    }

    // Se vier estranho, devolve os dígitos mesmo
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
          this.clientes = this.sortByNome(this.clientes.filter((x) => x.id !== id));
          this.applyFilter();
          this.loading = false;
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
}
