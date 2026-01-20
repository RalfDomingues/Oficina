import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

import { Cliente } from '../../../shared/models/cliente.model';

export type ClienteDetailsDialogData = { cliente: Cliente };

@Component({
  selector: 'app-cliente-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './cliente-details-dialog.component.html',
  styleUrl: './cliente-details-dialog.component.scss',
})
export class ClienteDetailsDialogComponent {
  /**
   * Dialog somente-leitura para exibir detalhes do cliente.
   * Alguns campos (email/telefone/cpf) podem não existir no model Cliente atual,
   * por isso acessamos de forma segura.
   */
  constructor(
    private readonly dialogRef: MatDialogRef<ClienteDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClienteDetailsDialogData
  ) {}

  /** Atalho para o cliente recebido pelo dialog. */
  get cliente(): Cliente {
    return this.data.cliente;
  }

  /** Campos opcionais (podem não existir no model Cliente). */
  get email(): string {
    return this.getClienteField('email');
  }

  get telefone(): string {
    return this.getClienteField('telefone');
  }

  get cpf(): string {
    return this.getClienteField('cpf');
  }

  /** Fecha o dialog. */
  close(): void {
    this.dialogRef.close();
  }

  /** Formata telefone para exibição (11 ou 10 dígitos). */
  get telefoneFormatado(): string {
    const digits = this.onlyDigits(this.telefone);
    if (!digits) return '-';
    if (digits.length === 11) return `${digits.slice(0, 2)} ${digits.slice(2, 7)}-${digits.slice(7)}`;
    if (digits.length === 10) return `${digits.slice(0, 2)} ${digits.slice(2, 6)}-${digits.slice(6)}`;
    return digits;
  }

  /** Formata CPF para exibição (11 dígitos). */
  get cpfFormatado(): string {
    const digits = this.onlyDigits(this.cpf);
    if (!digits) return '-';
    if (digits.length === 11) return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
    return digits;
  }

  // Helpers

  /** Lê um campo do cliente mesmo quando ele não está tipado no model. */
  private getClienteField(key: 'email' | 'telefone' | 'cpf'): string {
    return String((this.cliente as any)?.[key] ?? '');
  }

  /** Remove tudo que não for dígito. */
  private onlyDigits(value: unknown): string {
    return String(value ?? '').replace(/\D/g, '');
  }
}
