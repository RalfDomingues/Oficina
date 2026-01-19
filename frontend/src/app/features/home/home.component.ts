import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/auth/auth.service';
import { OrdemServicoService } from '../ordens-servico/data/ordem-servico.service';
import { OrdemServico } from '../../shared/models/ordem-servico.model';
import { PageResponse } from '../../shared/models/page.model';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  auth = inject(AuthService);
  user = this.auth.getUser();

  private osService = inject(OrdemServicoService);

  loading = false;
  errorMsg: string | null = null;

  osEmAndamento = 0;
  osConcluidasHoje = 0;

  ngOnInit(): void {
    this.carregarResumo();
  }

  private carregarResumo(): void {
    this.loading = true;
    this.errorMsg = null;

    this.osService.listar(0, 200).subscribe({
      next: (page: PageResponse<OrdemServico>) => {
        const lista: OrdemServico[] = page.content ?? [];

        this.osEmAndamento = lista.filter((os: OrdemServico) => os.status === 'EM_ANDAMENTO').length;

        const hoje = this.onlyDate(new Date());
        this.osConcluidasHoje = lista.filter((os: OrdemServico) =>
          os.status === 'CONCLUIDA' &&
          !!os.dataConclusao &&
          this.onlyDate(new Date(os.dataConclusao)) === hoje
        ).length;

        this.loading = false;
      },
      error: (err) => {
        this.loading = false;

        if (err?.status === 403) this.errorMsg = 'Você não tem permissão para visualizar as OS.';
        else this.errorMsg = 'Erro ao carregar o resumo da Home.';
      }
    });
  }

  private onlyDate(d: Date): string {
    return d.toISOString().slice(0, 10);
  }
}
