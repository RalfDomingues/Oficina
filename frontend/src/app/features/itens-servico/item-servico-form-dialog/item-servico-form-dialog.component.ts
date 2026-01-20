import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs/operators';

import { ItemServico } from '../data/item-servico.service';
import { OrdemServicoService } from '../../ordens-servico/data/ordem-servico.service';
import { ServicoService } from '../../servicos/data/servico.service';
import { OrdemServico } from '../../../shared/models/ordem-servico.model';
import { Servico } from '../../../shared/models/servico.model';

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
    MatProgressSpinnerModule,
  ],
  templateUrl: './item-servico-form-dialog.component.html',
  styleUrl: './item-servico-form-dialog.component.scss',
})
export class ItemServicoFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ItemServicoFormDialogComponent>);
  private readonly ordemServicoService = inject(OrdemServicoService);
  private readonly servicoService = inject(ServicoService);
  private readonly data = inject<ItemServicoFormData>(MAT_DIALOG_DATA);

  readonly isEdit = this.data.mode === 'edit';
  readonly item = this.isEdit && this.data.mode === 'edit' ? this.data.item : null;

  servicos: Servico[] = [];
  ordens: { id: number; numero: string }[] = [];
  maiorOrdemId: number | null = null;

  loading = true;

  form = this.fb.group({
    ordemServicoId: [null as number | null, [Validators.required, Validators.min(1)]],
    servicoId: [null as number | null, [Validators.required, Validators.min(1)]],
    quantidade: [1, [Validators.required, Validators.min(1)]],
    // No edit, o valor unitário já existe e pode ser alterado; no create pode ficar null (vem do serviço/back)
    valorUnitario: [null as number | null, this.isEdit ? [Validators.min(0.01)] : []],
    ativo: [true],
  });

  ngOnInit(): void {
    this.carregarDados();
  }

  /**
   * Carrega ordens e serviços para popular selects.
   * Regras:
   * - Create: preenche a OS com a maior id (última criada), se existir.
   * - Edit: OS fica travada e o form é preenchido com o item.
   */
  private carregarDados(): void {
    this.loading = true;

    let pendencias = 2;
    const done = () => {
      pendencias--;
      if (pendencias <= 0) this.loading = false;
    };

    this.ordemServicoService
      .listar(0, 5000)
      .pipe(finalize(done))
      .subscribe({
        next: (response) => {
          const ordens: OrdemServico[] = response.content ?? [];

          if (ordens.length > 0) {
            this.ordens = ordens.map((o) => ({ id: o.id, numero: `#${o.id}` }));
            this.maiorOrdemId = Math.max(...ordens.map((o) => o.id));

            if (!this.isEdit && this.maiorOrdemId) {
              this.form.get('ordemServicoId')?.setValue(this.maiorOrdemId);
            }

            if (this.isEdit && this.item) {
              this.form.get('ordemServicoId')?.disable();
              this.form.get('ordemServicoId')?.setValue(this.item.ordemServicoId);
            }
          }
        },
        error: () => {},
      });

    this.servicoService
      .listarTodos()
      .pipe(finalize(done))
      .subscribe({
        next: (servicos: Servico[]) => {
          this.servicos = (servicos ?? []).filter((s) => s.ativo);

          if (this.isEdit && this.item) {
            this.form.patchValue({
              servicoId: this.item.servicoId,
              quantidade: this.item.quantidade,
              valorUnitario: this.item.valorUnitario,
              ativo: this.item.ativo,
            });
          }
        },
        error: () => {},
      });
  }

  cancelar(): void {
    this.dialogRef.close(null);
  }

  /**
   * Retorna o payload do form para o componente pai decidir criar/atualizar no backend.
   * Usa getRawValue para incluir ordemServicoId mesmo quando o campo está disabled no modo edit.
   */
  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    this.dialogRef.close({
      ordemServicoId: raw.ordemServicoId ?? null,
      servicoId: raw.servicoId ?? null,
      quantidade: raw.quantidade ?? null,
      valorUnitario: raw.valorUnitario ?? null,
      ativo: raw.ativo ?? true,
    });
  }
}
