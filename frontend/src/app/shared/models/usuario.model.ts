export type PerfilUsuario = 'ADMIN' | 'SECRETARIA' | 'MECANICO';

export type Usuario = {
    id: number;
    nome: string;
    email: string;
    perfil: PerfilUsuario;
    ativo: boolean;
};
