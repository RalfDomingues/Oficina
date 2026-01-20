import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { PerfilUsuario, Usuario } from '../../../shared/models/usuario.model';

export type UsuarioFormDialogData =
  | { mode: 'create' }
  | { mode: 'edit'; usuario: Usuario };

@Component({
  selector: 'app-usuario-form-dialog',
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
  templateUrl: './usuario-form-dialog.component.html',
  styleUrl: './usuario-form-dialog.component.scss',
})
export class UsuarioFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<UsuarioFormDialogComponent>);
  private readonly data = inject<UsuarioFormDialogData>(MAT_DIALOG_DATA);

  readonly isEdit = this.data.mode === 'edit';

  perfis: PerfilUsuario[] = ['ADMIN', 'SECRETARIA', 'MECANICO'];

  private get usuarioEditado(): Usuario | null {
    return this.data.mode === 'edit' ? this.data.usuario : null;
  }

  /**
   * Form do dialog de usuário.
   * - Create: senha obrigatória.
   * - Edit: senha opcional (quando vazia, não altera no backend).
   */
  form = this.fb.group({
    nome: [this.usuarioEditado?.nome ?? '', [Validators.required, Validators.minLength(2)]],
    email: [this.usuarioEditado?.email ?? '', [Validators.required, Validators.email]],
    senha: ['', this.isEdit ? [] : [Validators.required, Validators.minLength(3)]],
    perfil: [this.usuarioEditado?.perfil ?? 'ADMIN', [Validators.required]],
    ativo: [this.usuarioEditado?.ativo ?? true],
  });

  cancelar(): void {
    this.dialogRef.close(null);
  }

  /**
   * Retorna o payload para o componente pai.
   * No edit, envia senha como null quando vazia para indicar "não alterar".
   */
  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();

    if (this.isEdit) {
      this.dialogRef.close({
        nome: raw.nome?.trim(),
        email: raw.email?.trim(),
        senha: raw.senha?.trim() || null,
        perfil: raw.perfil,
        ativo: raw.ativo,
      });
      return;
    }

    this.dialogRef.close({
      nome: raw.nome?.trim(),
      email: raw.email?.trim(),
      senha: raw.senha?.trim(),
      perfil: raw.perfil,
    });
  }
}
