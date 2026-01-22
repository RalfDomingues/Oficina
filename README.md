# 🚗 Sistema de Gestão de Oficina Mecânica

Sistema **Full Stack** para gerenciamento de uma **oficina mecânica**, composto por uma **API REST robusta** e uma **aplicação web moderna**, desenvolvido com foco em **boas práticas**, **segurança**, **organização de código**, **controle de acesso por perfil** e **análise gerencial**.

O projeto simula um cenário real de mercado, cobrindo desde a **operação diária da oficina** até **dashboards estratégicos para tomada de decisão**.

---

## 📌 Visão Geral do Sistema

O sistema permite:

- Autenticação segura de usuários por perfil
- Gestão completa de clientes, veículos, serviços e ordens de serviço
- Controle de acesso baseado em permissões
- Registro e auditoria de ações do sistema
- Visualização de indicadores operacionais e gerenciais
- Integração total entre backend e frontend via API REST

---

## 🧱 Arquitetura Geral

O projeto segue uma arquitetura **desacoplada**, dividida em duas camadas principais:

### 🔹 Backend — API REST

Responsável por:
- Regras de negócio
- Segurança e autenticação
- Persistência de dados
- Logs e auditoria
- Dashboards e análises

### 🔹 Frontend — Aplicação Web

Responsável por:
- Interface do usuário
- Experiência por perfil
- Consumo da API
- Controle de sessão e permissões
- Visualização de dados e indicadores

📡 A comunicação ocorre exclusivamente via **HTTP + JSON**, utilizando **JWT** para autenticação.

---

## 👥 Perfis de Acesso

O sistema trabalha com **controle de acesso por perfil**, aplicado tanto no backend quanto no frontend:

| Perfil | Descrição |
|------|-----------|
| **ADMIN** | Acesso total ao sistema |
| **SECRETARIA** | Operações administrativas e gerenciais |
| **MECÂNICO** | Consulta e execução de ordens de serviço |

🔐 **Rotas, ações e elementos visuais são exibidos dinamicamente conforme o perfil autenticado.**

---

## 🖥️ Funcionalidades Principais

- Autenticação com JWT (stateless)
- Dashboard com indicadores operacionais
- CRUD completo de:
  - Usuários
  - Clientes
  - Veículos
  - Serviços
  - Ordens de Serviço
- Associação de serviços às ordens
- Paginação e formulários reativos
- Tratamento centralizado de erros
- Logout automático por sessão expirada
- Logs de auditoria e segurança

---

## 🛠️ Tecnologias Utilizadas

### Backend
- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- SLF4J + Logback

### Frontend
- Angular 19
- TypeScript
- Angular Material
- RxJS
- HTML + SCSS

---

## 📊 Segurança e Auditoria

- Autenticação via JWT
- Autorização por perfil
- Interceptores de requisição no frontend
- Guards de rota
- Registro de:
  - Logins
  - Falhas de autenticação
  - Acessos negados
  - Uso de endpoints protegidos
- Limpeza automática de logs antigos

---

## ▶️ Execução do Projeto

### Pré-requisitos
- Java 21
- Node.js + npm
- PostgreSQL

### Backend
```bash
mvn clean install
mvn spring-boot:run
````


### API disponível em:

```
http://localhost:8080
```

### Frontend
```
npm install
npm start
```

### Aplicação disponível em:
```
http://localhost:4200
```

⚠️ A API precisa estar em execução para o frontend funcionar corretamente.

## 🎯 Objetivo do Projeto

Demonstrar domínio em arquitetura full stack

Aplicar boas práticas de segurança, organização e escalabilidade

Simular um sistema real utilizado em ambiente profissional

Servir como projeto de portfólio acadêmico e profissional
