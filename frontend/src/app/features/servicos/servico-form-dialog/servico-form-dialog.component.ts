import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export type ServicoFormData = {
  title: string;
  initial?: {
    nome: string;
    preco: number;
  };
};

@Component({
  selector: 'app-servico-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './servico-form-dialog.component.html',
  styleUrl: './servico-form-dialog.component.scss'
})
export class ServicoFormDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<ServicoFormDialogComponent>);
  data = inject<ServicoFormData>(MAT_DIALOG_DATA);

  form = this.fb.group({
    nome: [this.data.initial?.nome ?? '', [Validators.required, Validators.minLength(2)]],
    preco: [this.data.initial?.preco ?? 0, [Validators.required, Validators.min(0)]]
  });

  cancelar(): void {
    this.dialogRef.close(null);
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue());
  }
}
