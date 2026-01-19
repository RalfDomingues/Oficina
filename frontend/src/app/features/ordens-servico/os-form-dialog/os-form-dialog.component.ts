import { Component, Inject, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';

import { OrdemServico, StatusOrdemServico } from '../../../shared/models/ordem-servico.model';
import { ClienteService } from '../../clientes/data/cliente.service';
import { VeiculoService } from '../../veiculos/data/veiculo.service';
import { Veiculo } from '../../../shared/models/veiculo.model';

// ajuste se seu model for diferente
type Cliente = { id: number; nome: string; ativo?: boolean };

export type OsFormDialogData = {
    mode: 'create' | 'edit';
    initial?: OrdemServico;
};

@Component({
    selector: 'app-os-form-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
    ],
    templateUrl: './os-form-dialog.component.html',
    styleUrl: './os-form-dialog.component.scss',
})
export class OsFormDialogComponent implements OnInit {
    private fb = inject(FormBuilder);
    private clienteService = inject(ClienteService);
    private veiculoService = inject(VeiculoService);

    isEdit = false;
    isCreate = false;

    statusesEdit: StatusOrdemServico[] = ['ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA'];


    form: FormGroup;

    clientes: Cliente[] = [];
    veiculos: Veiculo[] = [];
    veiculosFiltrados: Veiculo[] = [];

    loadingClientes = false;
    loadingVeiculos = false;

    constructor(
        public ref: MatDialogRef<OsFormDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: OsFormDialogData
    ) {
        this.isEdit = data?.mode === 'edit';
        this.isCreate = data?.mode === 'create';

        this.form = this.fb.group({
            // CREATE only
            clienteId: [null as number | null, [Validators.required, Validators.min(1)]],
            veiculoId: [null as number | null, [Validators.required, Validators.min(1)]],
            valorEstimado: [null as number | null],

            // both
            descricao: ['', [Validators.required, Validators.minLength(2)]],

            // EDIT only
            status: ['ABERTA' as StatusOrdemServico],
            valorFinal: [null as number | null],
        });

        if (data?.initial) {
            this.form.patchValue({
                clienteId: data.initial.clienteId,
                veiculoId: data.initial.veiculoId,
                descricao: data.initial.descricao,
                status: data.initial.status,
                valorEstimado: data.initial.valorEstimado ?? null,
                valorFinal: data.initial.valorFinal ?? null,
            });
        }

        // trava cliente/veiculo no EDIT (back não contempla troca)
        if (this.isEdit) {
            this.form.get('clienteId')?.disable({ emitEvent: false });
            this.form.get('veiculoId')?.disable({ emitEvent: false });

            // no EDIT, status é obrigatório
            this.form.get('status')?.setValidators([Validators.required]);
            this.form.get('status')?.updateValueAndValidity({ emitEvent: false });

            // regra: se marcar CONCLUIDA, exige valorFinal
            this.form.get('status')?.valueChanges.subscribe((s: StatusOrdemServico) => {
                const ctrl = this.form.get('valorFinal');
                if (!ctrl) return;

                if (s === 'CONCLUIDA') {
                    ctrl.setValidators([Validators.required, Validators.min(0.01)]);
                } else {
                    ctrl.clearValidators();
                }
                ctrl.updateValueAndValidity({ emitEvent: false });
            });
        } else {
            // CREATE: status/valorFinal não usados pelo back -> deixa sem validator
            this.form.get('status')?.clearValidators();
            this.form.get('status')?.updateValueAndValidity({ emitEvent: false });
        }
    }

    ngOnInit(): void {
        if (this.isCreate) {
            this.carregarClientes();
            this.carregarVeiculos();

            // regra: mudou cliente => zera veiculo + filtra veículos do cliente
            this.form.get('clienteId')?.valueChanges.subscribe((cid) => {
                const clienteId = Number(cid || 0);

                this.form.get('veiculoId')?.setValue(null);
                this.form.get('veiculoId')?.markAsTouched();
                this.aplicarFiltroVeiculos(clienteId);
            });
        }
    }

    private carregarClientes(): void {
        this.loadingClientes = true;

        // backend é paginado
        this.clienteService.listarPaginado(0, 500).subscribe({
            next: (page) => {
                const lista = page.content ?? [];
                this.clientes = lista.filter((c: any) => c?.ativo !== false);
                this.loadingClientes = false;
            },
            error: () => {
                this.clientes = [];
                this.loadingClientes = false;
            }
        });
    }

    private carregarVeiculos(): void {
        this.loadingVeiculos = true;

        // defensivo: se teu backend retornar PageResponse, a gente trata também
        (this.veiculoService.listarTodos() as any).subscribe({
            next: (res: any) => {
                const lista: Veiculo[] = Array.isArray(res) ? res : (res?.content ?? []);
                this.veiculos = (lista ?? []).filter((v: any) => v?.ativo !== false);
                this.loadingVeiculos = false;

                const clienteId = Number(this.form.get('clienteId')?.value || 0);
                this.aplicarFiltroVeiculos(clienteId);
            },
            error: () => {
                this.veiculos = [];
                this.veiculosFiltrados = [];
                this.loadingVeiculos = false;
            }
        });
    }

    private aplicarFiltroVeiculos(clienteId: number): void {
        if (!clienteId) {
            this.veiculosFiltrados = [];
            return;
        }

        this.veiculosFiltrados = (this.veiculos ?? []).filter(
            (v) => Number(v.clienteId) === Number(clienteId)
        );
    }

    save() {
        this.ref.close(this.form.getRawValue());
    }
}
