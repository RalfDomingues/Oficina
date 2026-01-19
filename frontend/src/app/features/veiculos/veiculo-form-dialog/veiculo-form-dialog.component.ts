import { Component, Inject, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

import { Veiculo, TipoVeiculo } from '../../../shared/models/veiculo.model';
import { VeiculoService, VeiculoCreateDTO, VeiculoUpdateDTO } from '../data/veiculo.service';

import { ClienteService } from '../../clientes/data/cliente.service';
import { Cliente } from '../../../shared/models/cliente.model';

export type VeiculoFormDialogData =
  | { mode: 'create' }
  | { mode: 'edit'; veiculo: Veiculo };

@Component({
  selector: 'app-veiculo-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
  ],
  templateUrl: './veiculo-form-dialog.component.html',
  styleUrl: './veiculo-form-dialog.component.scss',
})
export class VeiculoFormDialogComponent implements OnInit {
  isEdit = false;
  loading = false;

  private fb = inject(FormBuilder);

  tipos: TipoVeiculo[] = ['CARRO', 'MOTO', 'CAMINHAO', 'UTILITARIO'];
  clientes: Cliente[] = [];

  // Mercosul: ABC1D23 (regex simples)
  private readonly placaRegex = /^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$/;

  // ✅ um único form (com ativo opcional)
  form = this.fb.group({
    placa: ['', [Validators.required, Validators.pattern(this.placaRegex)]],
    modelo: ['', [Validators.required]],
    marca: ['', [Validators.required]],
    ano: [new Date().getFullYear(), [Validators.required, Validators.min(1900), Validators.max(2100)]],
    tipo: [null as TipoVeiculo | null, [Validators.required]],
    clienteId: [null as number | null, [Validators.required]],
    ativo: [true],
  });

  constructor(
    private veiculoService: VeiculoService,
    private clienteService: ClienteService,
    private snack: MatSnackBar,
    private dialogRef: MatDialogRef<VeiculoFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VeiculoFormDialogData
  ) { }

  ngOnInit(): void {
    this.isEdit = this.data.mode === 'edit';

    this.carregarClientes();

    if (this.isEdit) {
      const v = (this.data as any).veiculo as Veiculo;

      this.form.patchValue({
        placa: v.placa,
        modelo: v.modelo,
        marca: v.marca,
        ano: v.ano,
        tipo: v.tipo,
        clienteId: v.clienteId,
        ativo: (v as any).ativo ?? true,
      });

      // se você não quer que altere placa e clienteId no edit:
      this.form.controls.placa.disable();
      this.form.controls.clienteId.disable();
    }
  }

  carregarClientes() {
    // teu ClienteService.listarTodos retorna Cliente[] (pelo que você mostrou)
    this.clienteService.listarTodos().subscribe({
      next: (list: Cliente[]) => {
        this.clientes = (list ?? []).filter((c: any) => c?.ativo !== false);
      },
      error: () => {
        this.clientes = [];
        this.snack.open('Erro ao carregar clientes', 'Fechar', { duration: 3000 });
      },
    });
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.snack.open('Verifique os campos do formulário.', 'Fechar', { duration: 2500 });
      return;
    }

    this.loading = true;

    if (!this.isEdit) {
      const raw = this.form.getRawValue();

      const dto: VeiculoCreateDTO = {
        placa: String(raw.placa ?? '').toUpperCase().trim(),
        modelo: String(raw.modelo ?? '').trim(),
        marca: String(raw.marca ?? '').trim(),
        ano: Number(raw.ano),
        tipo: raw.tipo as TipoVeiculo,
        clienteId: Number(raw.clienteId),
        // ativo: se teu create aceitar
        ...(raw.ativo != null ? ({ ativo: !!raw.ativo } as any) : {}),
      };

      this.veiculoService.criar(dto).subscribe({
        next: (created) => {
          this.loading = false;
          this.dialogRef.close(created);
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;
          const msg = (err.error as any)?.message ?? 'Erro ao criar veículo';
          this.snack.open(msg, 'Fechar', { duration: 3500 });
        },
      });

      return;
    }

    // edit
    const v = (this.data as any).veiculo as Veiculo;
    const raw = this.form.getRawValue();

    const dto: VeiculoUpdateDTO = {
      modelo: String(raw.modelo ?? '').trim(),
      marca: String(raw.marca ?? '').trim(),
      ano: Number(raw.ano),
      tipo: raw.tipo as TipoVeiculo,
      // ativo: se teu update aceitar
      ...(raw.ativo != null ? ({ ativo: !!raw.ativo } as any) : {}),
    };

    this.veiculoService.atualizar(v.id, dto).subscribe({
      next: (updated) => {
        this.loading = false;
        this.dialogRef.close(updated);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const msg = (err.error as any)?.message ?? 'Erro ao atualizar veículo';
        this.snack.open(msg, 'Fechar', { duration: 3500 });
      },
    });
  }

  cancelar() {
    this.dialogRef.close(null);
  }
}
