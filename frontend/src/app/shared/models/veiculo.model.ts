export type TipoVeiculo = 'CARRO' | 'MOTO' | 'CAMINHAO' | 'UTILITARIO';

export interface Veiculo {
  id: number;
  placa: string;
  modelo: string;
  marca: string;
  ano: number;
  tipo: TipoVeiculo;
  clienteId: number;
  ativo: boolean;
}
