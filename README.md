# 🚗 Oficina API

API REST para gerenciamento de uma **oficina mecânica**, desenvolvida em **Java + Spring Boot**, com foco em **boas práticas**, **arquitetura limpa**, **segurança com JWT**, **controle de acesso por perfil**, **logs de auditoria** e **dashboards gerenciais**.

---

## 📌 Visão Geral

Este projeto simula um sistema real de oficina mecânica, contemplando:

* Cadastro de clientes, veículos e serviços
* Gestão de ordens de serviço
* Controle de usuários e perfis
* Autenticação e autorização com JWT (stateless)
* Logs de auditoria por ação do usuário
* Dashboards para análise operacional

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**
* **Spring Boot / Spring Framework **
* **Spring Security** (JWT)
* **Spring Data JPA**
* **Hibernate**
* **PostgreSQL**
* **Maven**
* **SLF4J + Logback**

---

## 🏗️ Arquitetura do Projeto

O projeto segue uma separação clara de responsabilidades:

```
br.com.ralfdomingues.oficina
├── config
│   ├── security        # Configurações de segurança (JWT, filtros, handlers)
├── controller          # Controllers REST
├── domain
│   ├── auth            # Autenticação
│   ├── usuario         # Usuários e perfis
│   ├── cliente         # Clientes
│   ├── veiculo         # Veículos
│   ├── servico         # Serviços
│   ├── ordemservico    # Ordens de serviço
├── infra
│   ├── logging         # Logs e limpeza automática
├── repository          # Repositórios JPA
├── exception           # Exceções customizadas
```

---

## 🔐 Segurança e Perfis de Acesso

A autenticação é feita via **JWT**, sem uso de sessão.

### Perfis disponíveis:

* **ADMIN**

    * Acesso total ao sistema
* **SECRETARIA**

    * Operações administrativas
* **MECANICO**

    * Execução e consulta de ordens de serviço

### Exemplo de controle de acesso:

Exemplo de controle de acesso:
Endpoint	                 ADMIN	SECRETARIA	MECÂNICO
/usuarios/**	                ✅	      ❌	   ❌
/clientes/**	                ✅	      ✅	   ❌
/servicos/** (GET)	            ✅	      ✅	   ✅
/servicos/** (POST/PUT/DELETE)	✅	      ❌	   ❌
/ordens-servico/**	            ✅	      ✅	  Parcial

---

## 🔑 Autenticação

### Login

**POST** `/auth/login`

```json
{
  "email": "admin@oficina.com",
  "senha": "123456"
}
```

### Resposta

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "nome": "Admin Oficina",
  "email": "admin@oficina.com",
  "perfil": "ADMIN"
}
```

O token deve ser enviado nas próximas requisições:

```
Authorization: Bearer <token>
```

---

## 📊 Dashboards

A API disponibiliza endpoints de dashboard para análise gerencial, como:

* Total de ordens de serviço
* Ordens por status
* Ordens por mês
* Faturamento estimado

Acesso restrito a:

* **ADMIN**
* **SECRETARIA**

---

## 🧾 Logs do Sistema

O sistema registra automaticamente:

* Login com sucesso
* Falha de login (senha inválida, usuário desativado)
* Acesso negado por perfil
* Tentativas sem autenticação
* Chamadas a endpoints protegidos

### 📂 Local dos logs

```
/logs
  └── log-dd-MM-yyyy.txt
```

### ♻️ Limpeza automática

* Logs com mais de **7 dias** são removidos
* A limpeza ocorre **na inicialização da aplicação**

---

## ⚙️ Configuração do Projeto

### 1️⃣ Banco de Dados

Configure o PostgreSQL e crie o banco:

```sql
CREATE DATABASE oficina;
```

### 2️⃣ Configurações locais

Crie o arquivo:

```
application-local.yml
```

Exemplo:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/oficina
    username: postgres
    password: postgres

security:
  jwt:
    secret: SUA_SECRET_AQUI
    expiration: 86400000
```

⚠️ **Nunca versionar esse arquivo**

---

## ▶️ Como Executar

```bash
mvn clean install
mvn spring-boot:run
```

A API estará disponível em:

```
http://localhost:8080
```

---

## 🧪 Testes

Os endpoints podem ser testados via:

* Postman
* Insomnia
* Swagger (se configurado futuramente)

---

## 📌 Observações Finais

* Projeto estruturado para fácil manutenção
* Ideal para estudos de **Spring Boot + Segurança**
* Código organizado pensando em ambientes reais

---

## 👨‍💻 Autor

**Ralf Iran Domingues**
Projeto desenvolvido para fins acadêmicos e evolução profissional.

---

🚀 *Qualquer melhoria futura pode ser integrada facilmente à arquitetura atual.*
