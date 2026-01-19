export interface LoginRequest {
    email: string;
    senha: string;
}

export interface LoginResponse {
    token: string;
    id: number;
    nome: string;
    email: string;
    perfil: 'ADMIN' | 'SECRETARIA' | 'MECANICO';
}
