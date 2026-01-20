import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { finalize } from 'rxjs/operators';

import { AuthService } from '../../core/auth/auth.service';
import { OrdemServicoService } from '../ordens-servico/data/ordem-servico.service';
import { OrdemServico } from '../../shared/models/ordem-servico.model';
import { PageResponse } from '../../shared/models/page.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly osService = inject(OrdemServicoService);

  user = this.auth.getUser();

  loading = false;
  errorMsg: string | null = null;

  osEmAndamento = 0;
  osConcluidasHoje = 0;

  ngOnInit(): void {
    this.carregarResumo();
  }

  /**
   * Carrega um resumo simples para a Home:
   * - quantidade de OS em andamento
   * - quantidade de OS concluídas hoje
   */
  private carregarResumo(): void {
    this.loading = true;
    this.errorMsg = null;

    this.osService
      .listar(0, 200)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (page: PageResponse<OrdemServico>) => {
          const lista: OrdemServico[] = page.content ?? [];

          this.osEmAndamento = lista.filter((os) => os.status === 'EM_ANDAMENTO').length;

          const hoje = this.onlyDate(new Date());
          this.osConcluidasHoje = lista.filter(
            (os) =>
              os.status === 'CONCLUIDA' &&
              !!os.dataConclusao &&
              this.onlyDate(new Date(os.dataConclusao)) === hoje
          ).length;
        },
        error: (err) => {
          if (err?.status === 403) this.errorMsg = 'Você não tem permissão para visualizar as OS.';
          else this.errorMsg = 'Erro ao carregar o resumo da Home.';
        },
      });
  }

  private onlyDate(d: Date): string {
    return d.toISOString().slice(0, 10);
  }
}
