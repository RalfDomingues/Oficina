import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { HttpErrorResponse } from '@angular/common/http';

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
  private fb = inject(FormBuilder);
  private clienteService = inject(ClienteService);

  loading = false;
  errorMsg: string | null = null;

  readonly isEdit: boolean;

  // ✅ agora tem cpf obrigatório (11 dígitos)
  form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
    email: [''],
    telefone: [''],
  });

  constructor(
    private dialogRef: MatDialogRef<ClienteFormDialogComponent, Cliente | null>,
    @Inject(MAT_DIALOG_DATA) public data: ClienteFormDialogData
  ) {
    this.isEdit = data.mode === 'edit';

    if (data.mode === 'edit') {
      const c = data.cliente;

      this.form.patchValue({
        nome: c?.nome ?? '',
        cpf: String((c as any)?.cpf ?? '').replace(/\D/g, ''),
        email: (c as any)?.email ?? '',
        telefone: (c as any)?.telefone ?? '',
      });

      // ✅ normalmente CPF não muda (e evita problemas no back)
      this.form.controls.cpf.disable();
    }
  }

  close(): void {
    this.dialogRef.close(null);
  }

  onCpfInput(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    const digits = (input.value ?? '').replace(/\D/g, '').slice(0, 11);
    this.form.patchValue({ cpf: digits }, { emitEvent: false });
  }

  onTelefoneInput(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    const digits = (input.value ?? '').replace(/\D/g, '').slice(0, 11);

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

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMsg = null;

    const raw = this.form.getRawValue();

    const nome = (raw.nome ?? '').trim();
    const cpf = String(raw.cpf ?? '').replace(/\D/g, '');
    const email = (raw.email ?? '').trim();
    const telefone = String(raw.telefone ?? '').replace(/\D/g, '');

    if (!nome) {
      this.loading = false;
      this.errorMsg = 'Nome é obrigatório.';
      return;
    }

    // ✅ create
    if (this.data.mode === 'create') {
      if (!cpf || cpf.length !== 11) {
        this.loading = false;
        this.errorMsg = 'CPF é obrigatório (11 dígitos).';
        return;
      }

      const dto: ClienteCreateDTO = {
        nome,
        cpf, // ✅ manda cpf
        email: email || null,
        telefone: telefone || null,
      };

      this.clienteService.criar(dto).subscribe({
        next: (created) => {
          this.loading = false;
          this.dialogRef.close(created);
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;
          this.errorMsg = this.getBackendMsg(err) ?? 'Erro ao criar cliente.';
        },
      });

      return;
    }

    // ✅ edit
    const id = this.data.cliente?.id;
    if (id == null) {
      this.loading = false;
      this.errorMsg = 'Cliente inválido (sem ID).';
      return;
    }

    const dto: ClienteUpdateDTO = {
      nome,
      email: email || null,
      telefone: telefone || null,
      // cpf não vai no update (está desabilitado e geralmente não altera)
    };

    this.clienteService.atualizar(Number(id), dto).subscribe({
      next: (updated) => {
        this.loading = false;
        this.dialogRef.close(updated);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMsg = this.getBackendMsg(err) ?? 'Erro ao atualizar cliente.';
      },
    });
  }

  private getBackendMsg(err: HttpErrorResponse): string | null {
    return (
      (err.error as any)?.message ||
      (err.error as any)?.mensagem ||
      (err.error as any)?.erro ||
      null
    );
  }
}
