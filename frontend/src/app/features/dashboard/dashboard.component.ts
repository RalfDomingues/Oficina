import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { DashboardService } from './data/dashboard.service';
import { OrdemStatusResumo, OrdensPorMes, ServicoMaisUsado } from './data/dashboard.models';

type StatCard = { label: string; value: string };

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly dash = inject(DashboardService);

  loading = true;
  error: string | null = null;

  ordensStatus: OrdemStatusResumo[] = [];
  ordensPorMes: OrdensPorMes[] = [];
  servicosMaisUsados: ServicoMaisUsado[] = [];
  faturamentoTotal = 0;

  cards: StatCard[] = [];

  /**
   * Carrega o resumo do dashboard em paralelo (status, faturamento, serviços e ordens por mês).
   * Após carregar, normaliza/ordena os dados e monta os cards de estatística.
   */
  ngOnInit(): void {
    this.loading = true;
    this.error = null;

    forkJoin({
      ordensStatus: this.dash.ordensPorStatus(),
      faturamento: this.dash.faturamentoTotal(),
      servicos: this.dash.servicosMaisUsados(),
      porMes: this.dash.ordensPorMes(),
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: ({ ordensStatus, faturamento, servicos, porMes }) => {
          this.ordensStatus = ordensStatus ?? [];
          this.faturamentoTotal = Number(faturamento?.total ?? 0);
          this.servicosMaisUsados = this.ordenarServicosMaisUsados(servicos ?? []);
          this.ordensPorMes = this.ordenarOrdensPorMes(porMes ?? []);

          this.buildCards();
        },
        error: () => {
          this.error = 'Nao foi possivel carregar o dashboard.';
        },
      });
  }

  /** Ordena serviços por quantidade e limita o Top 10. */
  private ordenarServicosMaisUsados(lista: ServicoMaisUsado[]): ServicoMaisUsado[] {
    return [...(lista ?? [])]
      .sort((a, b) => (b.quantidade ?? 0) - (a.quantidade ?? 0))
      .slice(0, 10);
  }

  /** Ordena meses do mais recente para o mais antigo e limita os últimos 10. */
  private ordenarOrdensPorMes(lista: OrdensPorMes[]): OrdensPorMes[] {
    return [...(lista ?? [])]
      .sort((a, b) => this.valorMes(b.mes) - this.valorMes(a.mes))
      .slice(0, 10);
  }

  /** Converte "YYYY-MM" em timestamp para ordenação cronológica. */
  private valorMes(mes: string): number {
    return new Date(`${mes}-01`).getTime();
  }

  /** Monta cards a partir do resumo por status e faturamento total. */
  private buildCards(): void {
    const getQtd = (status: string) =>
      this.ordensStatus.find((s) => s.status === status)?.quantidade ?? 0;

    this.cards = [
      { label: 'Em aberto', value: String(getQtd('ABERTA')) },
      { label: 'Em andamento', value: String(getQtd('EM_ANDAMENTO')) },
      { label: 'Concluidas', value: String(getQtd('CONCLUIDA')) },
      { label: 'Canceladas', value: String(getQtd('CANCELADA')) },
      { label: 'Faturamento total', value: this.formatMoney(this.faturamentoTotal) },
    ];
  }

  /** Formata "YYYY-MM" para "MM/YYYY" para exibição. */
  formatMes(mes: string): string {
    const [y, m] = mes.split('-');
    if (!y || !m) return mes;
    return `${m}/${y}`;
  }

  private formatMoney(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  }
}
