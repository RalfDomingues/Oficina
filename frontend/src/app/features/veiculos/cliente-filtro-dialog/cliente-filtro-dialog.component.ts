import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatIconModule } from '@angular/material/icon';

import { ClienteService } from '../../clientes/data/cliente.service';
import { Cliente } from '../../../shared/models/cliente.model';

import { map, startWith } from 'rxjs/operators';
import { Observable } from 'rxjs';

type ClienteOption = { id: number; nome: string };

@Component({
  selector: 'app-cliente-filtro-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatAutocompleteModule,
    MatIconModule,
  ],
  templateUrl: './cliente-filtro-dialog.component.html',
  styleUrl: './cliente-filtro-dialog.component.scss',
})
export class ClienteFiltroDialogComponent {
  query = new FormControl<string | ClienteOption | null>('');

  private clientes: ClienteOption[] = [];
  filtered$!: Observable<ClienteOption[]>;

  constructor(
    private readonly ref: MatDialogRef<ClienteFiltroDialogComponent>,
    private readonly clienteService: ClienteService,
    @Inject(MAT_DIALOG_DATA) public data: { initialId?: number | null }
  ) {
    if (data?.initialId) this.query.setValue(String(data.initialId));

    this.setupFilterStream();

    this.clienteService.listarAtivos().subscribe({
      next: (list: Cliente[]) => {
        this.clientes = (list ?? []).map((c: any) => ({ id: Number(c.id), nome: c.nome ?? '' }));
        this.query.updateValueAndValidity({ emitEvent: true });
      },
      error: () => {
        this.clientes = [];
        this.query.updateValueAndValidity({ emitEvent: true });
      },
    });
  }

  /** Indica se existe algo preenchido no campo (texto ou opção selecionada). */
  get temValor(): boolean {
    const v = this.query.value;
    if (v == null) return false;
    if (typeof v === 'string') return v.trim().length > 0;
    return true;
  }

  limpar(): void {
    this.query.setValue('');
  }

  /**
   * Stream do autocomplete:
   * - texto: filtra por nome ou por id (quando o usuário digita números)
   * - objeto: mantém a seleção
   */
  private setupFilterStream(): void {
    this.filtered$ = this.query.valueChanges.pipe(
      startWith(this.query.value),
      map((value) => this.filter(value))
    );
  }

  private filter(value: string | ClienteOption | null | undefined): ClienteOption[] {
    if (value && typeof value === 'object') return [value];

    const txt = String(value ?? '').trim().toLowerCase();
    if (!txt) return this.clientes.slice(0, 20);

    if (/^\d+$/.test(txt)) {
      const id = Number(txt);
      const exact = this.clientes.find((c) => c.id === id);
      return exact ? [exact] : [];
    }

    return this.clientes
      .filter((c) => (c.nome ?? '').toLowerCase().includes(txt))
      .slice(0, 20);
  }

  displayWith = (c: ClienteOption | string | null): string => {
    if (!c) return '';
    return typeof c === 'string' ? c : c.nome;
  };

  selecionar(c: ClienteOption): void {
    this.ref.close({ clienteId: c.id });
  }

  /**
   * Permite aplicar o que foi digitado:
   * - número => tenta usar como id
   * - texto => pega o primeiro cliente cujo nome contém o termo
   */
  aplicarDigitado(): void {
    const v = this.query.value;

    if (v && typeof v === 'object') {
      this.ref.close({ clienteId: v.id });
      return;
    }

    const raw = String(v ?? '').trim();
    if (!raw) return;

    if (/^\d+$/.test(raw)) {
      const id = Number(raw);
      if (Number.isFinite(id) && id > 0) this.ref.close({ clienteId: id });
      return;
    }

    const txt = raw.toLowerCase();
    const first = this.clientes.find((c) => (c.nome ?? '').toLowerCase().includes(txt));
    if (first) this.ref.close({ clienteId: first.id });
  }

  cancelar(): void {
    this.ref.close(null);
  }
}
