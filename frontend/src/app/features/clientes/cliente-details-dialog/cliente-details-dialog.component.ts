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
  constructor(
    private dialogRef: MatDialogRef<ClienteDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClienteDetailsDialogData
  ) { }

  // Angular template fica mais feliz com getters simples
  get cliente(): Cliente {
    return this.data.cliente;
  }

  get email(): string {
    return ((this.data.cliente as any)?.email ?? '') as string;
  }

  get telefone(): string {
    return ((this.data.cliente as any)?.telefone ?? '') as string;
  }

  close(): void {
    this.dialogRef.close();
  }

  get telefoneFormatado(): string {
    const digits = String(this.telefone ?? '').replace(/\D/g, '');
    if (!digits) return '-';
    if (digits.length === 11) return `${digits.slice(0, 2)} ${digits.slice(2, 7)}-${digits.slice(7)}`;
    if (digits.length === 10) return `${digits.slice(0, 2)} ${digits.slice(2, 6)}-${digits.slice(6)}`;
    return digits;
  }

}
