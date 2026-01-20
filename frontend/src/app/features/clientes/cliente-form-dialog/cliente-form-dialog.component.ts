import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

import { ClienteService, ClienteCreateDTO, ClienteUpdateDTO } from '../data/cliente.service';
import { Cliente } from '../../../shared/models/cliente.model';

export type ClienteFormDialogData =
  | { mode: 'create' }
  | { mode: 'edit'; cliente: Cliente };

@Component({
  selector: 'app-cliente-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './cliente-form-dialog.component.html',
  styleUrl: './cliente-form-dialog.component.scss',
})
export class ClienteFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly clienteService = inject(ClienteService);

  // Estado de UI: spinner e mensagem de erro exibida no dialog
  loading = false;
  errorMsg: string | null = null;

  // Define comportamento do dialog (criação ou edição)
  readonly isEdit: boolean;

  // CPF é obrigatório e aceitamos apenas 11 dígitos (sem máscara no valor final)
  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
    email: [''],
    telefone: [''],
  });

  constructor(
    private readonly dialogRef: MatDialogRef<ClienteFormDialogComponent, Cliente | null>,
    @Inject(MAT_DIALOG_DATA) public data: ClienteFormDialogData
  ) {
    this.isEdit = data.mode === 'edit';

    if (data.mode === 'edit') {
      const c = data.cliente;

      // Carrega dados no form; CPF fica somente leitura para evitar inconsistência no back
      this.form.patchValue({
        nome: c?.nome ?? '',
        cpf: this.onlyDigits((c as any)?.cpf).slice(0, 11),
        email: (c as any)?.email ?? '',
        telefone: (c as any)?.telefone ?? '',
      });

      this.form.controls.cpf.disable();
    }
  }

  close(): void {
    this.dialogRef.close(null);
  }

  // Garante que CPF fique sempre com dígitos e limite 11
  onCpfInput(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    this.form.patchValue({ cpf: this.onlyDigits(input.value).slice(0, 11) }, { emitEvent: false });
  }

  // Máscara simples em tempo real; ao salvar, enviamos apenas dígitos
  onTelefoneInput(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    const digits = this.onlyDigits(input.value).slice(0, 11);

    let masked = digits;

    if (digits.length >= 3) {
      if (digits.length <= 10) {
        masked = `${digits.slice(0, 2)} ${digits.slice(2, 6)}${digits.length > 6 ? '-' + digits.slice(6) : ''}`;
      } else {
        masked = `${digits.slice(0, 2)} ${digits.slice(2, 7)}-${digits.slice(7)}`;
      }
    }

    this.form.patchValue({ telefone: masked }, { emitEvent: false });
  }

  // Fluxo: valida -> normaliza campos -> create ou update -> fecha dialog com retorno
  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMsg = null;
    this.loading = true;

    const raw = this.form.getRawValue();

    const nome = (raw.nome ?? '').trim();
    const cpf = this.onlyDigits(raw.cpf).slice(0, 11);
    const email = (raw.email ?? '').trim();
    const telefoneDigits = this.onlyDigits(raw.telefone).slice(0, 11);

    if (!nome) {
      this.loading = false;
      this.errorMsg = 'Nome é obrigatório.';
      return;
    }

    // Create: CPF obrigatório e enviado para o back
    if (this.data.mode === 'create') {
      if (!cpf || cpf.length !== 11) {
        this.loading = false;
        this.errorMsg = 'CPF é obrigatório (11 dígitos).';
        return;
      }

      const dto: ClienteCreateDTO = {
        nome,
        cpf,
        email: email || null,
        telefone: telefoneDigits || null,
      };

      this.clienteService
        .criar(dto)
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: (created) => this.dialogRef.close(created),
          error: (err: HttpErrorResponse) => {
            this.errorMsg = this.getBackendMsg(err) ?? 'Erro ao criar cliente.';
          },
        });

      return;
    }

    // Edit: CPF não é alterado; atualiza apenas campos permitidos
    const id = this.data.cliente?.id;
    if (id == null) {
      this.loading = false;
      this.errorMsg = 'Cliente inválido (sem ID).';
      return;
    }

    const dto: ClienteUpdateDTO = {
      nome,
      email: email || null,
      telefone: telefoneDigits || null,
    };

    this.clienteService
      .atualizar(Number(id), dto)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (updated) => this.dialogRef.close(updated),
        error: (err: HttpErrorResponse) => {
          this.errorMsg = this.getBackendMsg(err) ?? 'Erro ao atualizar cliente.';
        },
      });
  }

  // Alguns backends retornam "message" ou "mensagem" ou "erro"
  private getBackendMsg(err: HttpErrorResponse): string | null {
    return (err.error as any)?.message || (err.error as any)?.mensagem || (err.error as any)?.erro || null;
  }

  private onlyDigits(value: unknown): string {
    return String(value ?? '').replace(/\D/g, '');
  }
}
