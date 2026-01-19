import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  standalone: true,
  selector: 'app-valor-final-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  template: `
    <h2 mat-dialog-title>Informar valor final</h2>

    <div mat-dialog-content>
      <p>Para concluir a OS, informe o valor final.</p>

      <form [formGroup]="form">
        <mat-form-field appearance="outline" style="width: 100%">
          <mat-label>Valor final</mat-label>
          <input matInput type="number" formControlName="valor" />
          <mat-hint>Ex: 350.00</mat-hint>
        </mat-form-field>
      </form>
    </div>

    <div mat-dialog-actions align="end">
      <button mat-button (click)="ref.close(null)">Cancelar</button>
      <button mat-raised-button color="primary"
              [disabled]="form.invalid"
              (click)="ref.close(form.value.valor)">
        Confirmar
      </button>
    </div>
  `,
})
export class ValorFinalDialogComponent {
  form: FormGroup;

  constructor(
    fb: FormBuilder,
    public ref: MatDialogRef<ValorFinalDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { valorAtual?: number | null }
  ) {
    this.form = fb.group({
      valor: [data?.valorAtual ?? null, [Validators.required, Validators.min(0.01)]],
    });
  }
}
