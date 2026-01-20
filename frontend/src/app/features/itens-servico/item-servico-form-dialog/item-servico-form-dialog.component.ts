import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ItemServico } from '../../../shared/models/item-servico.model';

export type ItemServicoFormData =
  | { mode: 'create' }
  | { mode: 'edit'; item: ItemServico };

@Component({
  selector: 'app-item-servico-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './item-servico-form-dialog.component.html',
  styleUrl: './item-servico-form-dialog.component.scss',
})
export class ItemServicoFormDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<ItemServicoFormDialogComponent>);
  data = inject<ItemServicoFormData>(MAT_DIALOG_DATA);

  readonly isEdit = this.data.mode === 'edit';

  form = this.fb.group({
    ordemServicoId: [
      this.data.mode === 'edit' ? this.data.item.ordemServicoId : null,
      [Validators.required, Validators.min(1)],
    ],
    servicoId: [
      this.data.mode === 'edit' ? this.data.item.servicoId : null,
      [Validators.required, Validators.min(1)],
    ],
    quantidade: [
      this.data.mode === 'edit' ? this.data.item.quantidade : 1,
      [Validators.required, Validators.min(1)],
    ],
    valorUnitario: [
      this.data.mode === 'edit' ? this.data.item.valorUnitario : null,
      this.data.mode === 'edit' ? [Validators.min(0.01)] : [],
    ],
    ativo: [this.data.mode === 'edit' ? this.data.item.ativo : true],
  });

  ngOnInit(): void {
    if (this.isEdit) {
      this.form.controls.ordemServicoId.disable();
    }
  }

  cancelar(): void {
    this.dialogRef.close(null);
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    this.dialogRef.close({
      ...raw,
      ordemServicoId: raw.ordemServicoId ? Number(raw.ordemServicoId) : null,
      servicoId: raw.servicoId ? Number(raw.servicoId) : null,
      quantidade: raw.quantidade ? Number(raw.quantidade) : null,
      valorUnitario: raw.valorUnitario != null ? Number(raw.valorUnitario) : null,
    });
  }
}
