# 🚘 Oficina Frontend

Aplicação web para gerenciamento de uma **oficina mecânica**, desenvolvida em **Angular**, com foco em **usabilidade**, **organização de telas**, **experiência do usuário por perfil** e **integração total com a Oficina API**.

Este projeto representa a camada visual e operacional do sistema, consumindo a API REST e aplicando regras de acesso conforme o perfil do usuário.

---

## 📌 Visão Geral

O frontend cobre todo o fluxo operacional da oficina, permitindo:

- Autenticação de usuários por perfil
- Visualização de dashboards gerenciais
- Gestão de usuários, clientes e veículos
- Controle de serviços e ordens de serviço
- Manipulação de itens de serviço vinculados às OS
- Interface responsiva e orientada à produtividade

---

## 👥 Perfis de Acesso

A aplicação respeita os mesmos perfis definidos na API:

- **ADMIN**
  - Acesso total ao sistema
- **SECRETARIA**
  - Operações administrativas e gerenciamento
- **MECÂNICO**
  - Consulta e execução de ordens de serviço

🔐 **Rotas, ações e botões são exibidos dinamicamente conforme o perfil autenticado.**

---

## 🖥️ Funcionalidades Principais

- Tela de login com autenticação JWT
- Dashboard com indicadores operacionais
- CRUD de:
  - Usuários
  - Clientes
  - Veículos
  - Serviços
  - Ordens de Serviço
- Associação de itens de serviço às OS
- Paginação, formulários reativos e dialogs modais
- Tratamento centralizado de erros e sessão expirada

---

## 🛠️ Tecnologias Utilizadas

- **Angular 19**
- **TypeScript**
- **Angular Material**
- **RxJS**
- **HTML + SCSS**

---

## 🏗️ Estrutura do Projeto

Organização pensada para escalabilidade e fácil manutenção:

```
src/
├── app/
│   ├── core/          # Autenticação, guards, interceptors
│   ├── features/      # Módulos de negócio (usuarios, clientes, veiculos, etc)
│   ├── shared/        # Componentes, models e utilitários reutilizáveis
├── environments/      # Configuração de ambientes (apiUrl)
```
🔗 Integração com a API

## A URL base da API é definida nos arquivos de ambiente:

src/environments/environment.ts
src/environments/environment.development.ts

Exemplo:
export const environment = {
  apiUrl: 'http://localhost:8080'
};


 ⚠️ A API precisa estar em execução para o frontend funcionar corretamente.

## ▶️ Como Executar
### 1️⃣ Instalar dependências
npm install

### 2️⃣ Executar em ambiente de desenvolvimento
npm start


A aplicação estará disponível em:

http://localhost:4200

### 🔒 Segurança no Frontend

Token JWT armazenado no cliente

Interceptor HTTP para envio automático do token

Guards de rota por autenticação e perfil

Logout automático em caso de token inválido ou expirado

## 📌 Observações Finais

Frontend totalmente desacoplado do backend

Arquitetura modular e organizada

Fácil expansão para novos módulos e dashboards

Projeto ideal para estudo e demonstração de Angular em cenários reais
