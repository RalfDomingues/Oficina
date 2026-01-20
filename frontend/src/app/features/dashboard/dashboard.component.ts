import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';

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
  private dash = inject(DashboardService);

  loading = true;
  error: string | null = null;

  // dados crus
  ordensStatus: OrdemStatusResumo[] = [];
  ordensPorMes: OrdensPorMes[] = [];
  servicosMaisUsados: ServicoMaisUsado[] = [];
  faturamentoTotal = 0;

  // dados “prontos” pra UI
  cards: StatCard[] = [];

  ngOnInit(): void {
    this.loading = true;
    this.error = null;

    forkJoin({
      ordensStatus: this.dash.ordensPorStatus(),
      faturamento: this.dash.faturamentoTotal(),
      servicos: this.dash.servicosMaisUsados(),
      porMes: this.dash.ordensPorMes(),
    }).subscribe({
      next: ({ ordensStatus, faturamento, servicos, porMes }) => {
        this.ordensStatus = ordensStatus;
        this.faturamentoTotal = Number(faturamento.total ?? 0);
        this.servicosMaisUsados = servicos;
        this.ordensPorMes = porMes;

        this.buildCards();

        this.loading = false;
      },
      error: () => {
        this.error = 'Não foi possível carregar o dashboard.';
        this.loading = false;
      },
    });
  }

  private buildCards(): void {
  const getQtd = (status: string) =>
    this.ordensStatus.find((s) => s.status === status)?.quantidade ?? 0;

  const aberta = getQtd('ABERTA');           // ← Adicionar
  const emAndamento = getQtd('EM_ANDAMENTO');
  const concluida = getQtd('CONCLUIDA');
  const cancelada = getQtd('CANCELADA');

  this.cards = [
    { label: 'Em aberto', value: String(aberta) },      // ← Adicionar aqui PRIMEIRO
    { label: 'Em andamento', value: String(emAndamento) },
    { label: 'Concluídas', value: String(concluida) },
    { label: 'Canceladas', value: String(cancelada) },
    { label: 'Faturamento total', value: this.formatMoney(this.faturamentoTotal) },
  ];
}

  formatMes(mes: string): string {
    // "2025-12" -> "12/2025"
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
