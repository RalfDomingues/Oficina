import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

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

  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  loading = false;
  errorMsg: string | null = null;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(3)]]
  });

  submit(): void {
    this.errorMsg = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    const redirectTo = this.route.snapshot.queryParamMap.get('redirectTo') || '/';

    this.auth.login({
      email: this.form.value.email!,
      senha: this.form.value.senha!
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(redirectTo);
      },
      error: (err) => {
        this.loading = false;

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
