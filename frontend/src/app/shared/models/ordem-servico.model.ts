export type StatusOrdemServico = 'ABERTA' | 'EM_ANDAMENTO' | 'CONCLUIDA' | 'CANCELADA';

export interface OrdemServico {
    id: number;
    clienteId: number;
    veiculoId: number;
    status: StatusOrdemServico;
    descricao: string;
    valorFinal: number | null;
    valorEstimado: number | null;
    dataAbertura: string;
    dataConclusao: string | null;
}
