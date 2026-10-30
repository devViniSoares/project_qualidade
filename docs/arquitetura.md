# Arquitetura

## Contexto

```mermaid
flowchart LR
    U[Usuário] --> S[Organização de Recursos]
    G[Gestor] --> S
    A[Administrador] --> S
    S --> B[(H2 local)]
```

## Componentes

```mermaid
flowchart TB
    UI[HTML CSS JavaScript] --> SEC[Spring Security e CSRF]
    SEC --> API[API REST e validação]
    API --> US[Usuarios e BCrypt]
    API --> AG[Agenda e regras]
    AG --> TX[TransactionTemplate e JdbcTemplate]
    US --> DB[(H2)]
    TX --> DB
```

## Sequência crítica: reserva

```mermaid
sequenceDiagram
    participant U as Usuário
    participant API as API e segurança
    participant AG as Agenda
    participant DB as H2
    U->>API: POST reservas com autenticação e CSRF
    API->>API: Validar campos
    API->>AG: reservar usuário e intervalo
    Note over AG: Monitor exclusivo na instância
    AG->>AG: Validar intervalo e início futuro
    AG->>DB: Iniciar transação e verificar conflitos
    alt Recurso inativo ou conflito
        DB-->>AG: Indisponível
        AG-->>API: Falha e rollback
        API-->>U: 409 Conflict
    else Disponível
        AG->>DB: Inserir reserva e commit
        DB-->>AG: Registro persistido
        AG-->>API: Reserva
        API-->>U: 201 Created
    end
    Note over AG: Liberar monitor após commit
```

## Sequência crítica: cancelamento

```mermaid
sequenceDiagram
    participant U as Usuário ou gestor
    participant API as API e segurança
    participant AG as Agenda
    participant DB as H2
    U->>API: POST cancelamento com CSRF
    API->>AG: cancelar id e usuário autenticado
    AG->>DB: Consultar reserva
    alt Outro titular e perfil USUARIO
        AG-->>API: Acesso negado
        API-->>U: 403 Forbidden
    else Titular ou gestão
        AG->>DB: Marcar CANCELADA
        DB-->>AG: Histórico preservado
        AG-->>API: Reserva cancelada
        API-->>U: 200 OK
    end
```

## Modelo resumido

```mermaid
erDiagram
    USUARIOS ||--o{ RESERVAS : solicita
    RECURSOS ||--o{ RESERVAS : ocupa
    USUARIOS {
        bigint id PK
        string nome
        string email UK
        string senha_hash
        string perfil
    }
    RECURSOS {
        bigint id PK
        string nome
        string tipo
        int capacidade
        boolean ativo
    }
    RESERVAS {
        bigint id PK
        bigint usuario_id FK
        bigint recurso_id FK
        datetime inicio
        datetime fim
        string status
    }
```

Campos completos em [schema.sql](../src/main/resources/schema.sql).

## Decisões e consequências

| ID | Decisão | Motivo e consequência |
|---|---|---|
| ADR01 | Java 21 e Spring Boot 3.5.16 | Testes e segurança integrados; CI em Java 21, execução local em Java 23 |
| ADR02 | JDBC e H2 em arquivo | Sem serviço externo para reproduzir; não representa banco distribuído |
| ADR03 | Monitor cobrindo TransactionTemplate | Impede janela entre validação e commit numa instância; não cobre escritas externas ou múltiplas JVMs |
| ADR04 | OffsetDateTime | Comparações por instante; frontend converte data local do dispositivo para UTC |
| ADR05 | HTTP Basic e CSRF padrão do Spring Security | Evita token próprio; exige HTTPS fora de localhost e inclui custo BCrypt por requisição |
| ADR06 | Histórico CANCELADA preservado | Cancelamento idempotente; exclusão de recurso com histórico é rejeitada |

O servidor escuta 127.0.0.1 por padrão. Não há HTTPS, rate limiting ou garantia distribuída. A arquitetura não autoriza declarar prontidão de produção.
