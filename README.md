# Hermes Matchmaking System (V1)

![Java](https://img.shields.io/badge/java-000000.svg?style=for-the-badge&logo=openjdk&logoColor=orange)
![JDBC](https://img.shields.io/badge/Jdbc-000000?style=for-the-badge&logo=openjdk&logoColor=orange)
![JDBC](https://img.shields.io/badge/Redis-000000?style=for-the-badge&logo=redis&logoColor=red)
![SpringBoot](https://img.shields.io/badge/springboot-000000?style=for-the-badge&logo=springboot&logoColor=green)
![SpringSecurity](https://img.shields.io/badge/Spring%20Security-000000?style=for-the-badge&logo=spring-security&logoColor=green)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=spring-security&logoColor=green)
![JUnit](https://img.shields.io/badge/junit-000000?style=for-the-badge&logoColor=green)
![Mockito](https://img.shields.io/badge/mockito-000000?style=for-the-badge&logoColor=green)
![TestContainers](https://img.shields.io/badge/testcontainers-000000?style=for-the-badge&logo=docker&logoColor=blue)
![Docker](https://img.shields.io/badge/docker-000000?style=for-the-badge&logo=docker&logoColor=blue)
![MySQL](https://img.shields.io/badge/MySQL-000000.svg?style=for-the-badge&logo=MySQL&logoColor=blue)
![repo size](https://img.shields.io/github/repo-size/GuinhoFSilva/hermes?style=for-the-badge&color=000000&labelColor=000000) 


> O **Hermes Matchmaking (V1)** é o microsserviço de matchmaking do ecossistema Olympus.
> Nesta primeira versão, recebe os jogadores em uma fila de espera e os agrupa automaticamente em partidas quando existem jogadores suficientes.

## Features

- Autenticação baseada em JWT
- Matchmaking utilizando Redis como fila
- Recuperação de partidas do jogador autenticado
- Tratamento centralizado de exceções

## Fluxo do Matchmaking

```text
┌──────────────────────────────┐
│          Player A            │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│      Entrar na fila Redis    │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│   Já há 1 jogador na fila?   │
└──────────┬───────────┬────────┘
           │ Sim       │ Não
           ▼           ▼
┌──────────────────┐  ┌──────────────────────────────┐
│ Remover jogador  │  │ Aguardar próximo jogador     │
│ da fila          │  └──────────────────────────────┘
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────┐
│         Criar Match          │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│   Persistir Match no MySQL   │
└──────────────┬───────────────┘
               │
               ▼
        Match disponível
```


## Arquitetura

- Clean Architecture
- Domain-Driven Design (DDD)
- Dependency Injection
- JWT Authentication
- JDBC Persistence
- Redis Queue
- REST API

## Tecnologias

- Java 21
- Spring Boot
- Spring Security
- JDBC
- MySQL
- Redis
- JUnit 5
- Mockito
- TestContainers
- JWT
- Docker

## Testes

O projeto possui:

- Testes unitários do domínio
- Testes unitários dos casos de uso
- Testes de integração utilizando:
  - Testcontainers
  - MySQL
  - MockMvc
- Testes dos Fluxos HTTP completos
 
---

### Endpoints
|Método |Endpoint |Auth |Descrição
|--------|----------|-----------|-----------|
| GET    | /v1/matches/{id} |  ✅ | Retornar uma única partida
| GET    | /v1/matches/me |  ✅ | Retornar Todas as Partidas de um jogador
| POST   | /v1/match/queue |  ✅ | Entrar em uma fila
| DELETE   | /v1/match/queue    |  ✅ | Sair de uma fila

---

## Entidades

### Match

| Campo | Tipo |
|--------|------|
| id | UUID |
| status | String |
| createdAt | DateTime |
| endAt | DateTime |


### Participants

| Campo | Tipo |
|--------|------|
| id | int |
| id_match | UUID |
| id_player | UUID |

---

# Casos de Uso
> Nota: Esses são os casos de uso referentes à versão 1 do projeto.

###### **JoinQueue**
Objetivo: Colocar o jogador em uma fila de espera;

*Entrada:*
- Token JWT;

*Processamento:*
- O sistema valida o Token JWT;
- O sistema extrai o ID do player pelo Token;
- O sistema valida se o usuário não está na fila;
- O sistema adiciona o jogador à fila;
- O sistema retorna uma mensagem de confirmação;

*Saída:*
- DTO hasMatch;

*Erros:*
- Jogador não existe;
- Jogador não está autenticado;
- Jogador já está na fila;

*Regras de Negócio:*
- O jogador precisa estar cadastrado no sistema; 
- O jogador precisa estar autenticado no sistema;
- Se a fila conter dois jogadores válidos, uma partida é criada e persistida;

###### **LeaveQueue**
Objetivo: Tirar o jogador de uma fila;

*Entrada:*
- Token JWT;

*Processamento:*
- O sistema recebe o token JWT do usuário;
- O sistema valida o token JWT;
- O sistema valida se o usuário está em uma fila;
- O sistema remove o jogador da fila;

*Saída:*
- void;

*Erros:*
- Jogador não está autenticado;
- Jogador não está em uma fila;

*Regras de Negócio:*
- O jogador precisa estar autenticado no sistema;
- O jogador deve estar em uma fila;


###### **GetMatch**
Objetivo: Retornar informações de uma partida específica.

*Entrada:
- JWT
- Id da partida;

*Processamento:*
- O sistema recebe o token JWT;
- O sistema recebe o id da partida;
- O sistema valida o token recebido;
- O sistema identifica o jogador associado ao token;
- O sistema verifica se a partida existe;
- O sistema verifica se a partida pertence ao jogador autenticado;
- O sistema retorna os dados da partida;

*Saída:*
- Id;
- Participantes;
- Status;
- CreatedAt;
- EndAt;

*Erros:*
- Partida não encontrada;
- JWT inválido;
- JWT expirado;

*Regras de Negócio:*
- A partida retornada deve corresponder à partida dona do Id;
- A partida deve estar relacionada ao jogador autenticado;

###### **GetPlayerMatches**
Objetivo: Retornar informações de todas as partidas de um jogador específico.

*Entrada:
- Token JWT;

*Processamento:*
- O sistema recebe o token JWT do jogador;
- O sistema verifica se o jogador existe;
- O sistema busca todas as partidas relacionadas ao id do jogador;
- O sistema retorna os dados das patidas;

*Saída:*
- Lista de partidas daquele jogador;

*Erros:*
- JWT inválido;
- JWT expirado;
- Jogador não encontrado;

*Regras de Negócio:*
- As partidas retornadas devem corresponder às partidas do jogador autenticado;
- Um jogador não pode consultar todas as partidas de outro jogador;
