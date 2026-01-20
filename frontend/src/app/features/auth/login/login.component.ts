import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { finalize } from 'rxjs/operators';

import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  // Injeções via inject() para manter o componente standalone e enxuto.
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  // Estado de UI: spinner e mensagem de erro amigável.
  loading = false;
  errorMsg: string | null = null;

  /**
   * Formulário de login.
   * Regras:
   * - email obrigatório e válido
   * - senha obrigatória (mínimo 3)
   */
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(3)]]
  });

  // Acessores para reduzir "!" espalhado e deixar o submit mais legível.
  get email(): string {
    return this.form.value.email ?? '';
  }

  get senha(): string {
    return this.form.value.senha ?? '';
  }

  /** Resolve a rota de redirecionamento pós-login. */
  private get redirectTo(): string {
    return this.route.snapshot.queryParamMap.get('redirectTo') || '/';
  }

  /**
   * Submete o login.
   * - Valida form
   * - Chama AuthService
   * - Redireciona para redirectTo (query param) ou "/"
   */
  submit(): void {
    this.errorMsg = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    this.auth
      .login({ email: this.email, senha: this.senha })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => this.router.navigateByUrl(this.redirectTo),
        error: (err) => {
          // Mapeia erros comuns para mensagens compreensíveis ao usuário.
          if (err?.status === 401) {
            this.errorMsg = 'Email ou senha inválidos.';
          } else if (err?.status === 0) {
            this.errorMsg = 'Não foi possível conectar ao servidor (Spring está rodando?).';
          } else {
            this.errorMsg = 'Erro ao autenticar. Tente novamente.';
          }
        }
      });
  }
}
