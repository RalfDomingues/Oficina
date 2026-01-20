export type OrdemStatusResumo = {
    status: 'CANCELADA' | 'CONCLUIDA' | 'EM_ANDAMENTO' | 'ABERTA' | string;
    quantidade: number;
};

export type FaturamentoResumo = {
    total: number;
};

export type ServicoMaisUsado = {
    nomeServico: string;
    quantidade: number;
};

export type OrdensPorMes = {
    mes: string; // formato "YYYY-MM"
    quantidade: number;
};
