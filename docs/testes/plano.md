# Plano de testes

## Critérios

Validar [requisitos](../requisitos.md) e [RTM](../rtm.md). Antes de cada commit executar `./mvnw -B verify` (`.\mvnw.cmd -B verify` no PowerShell). Aceitação automatizada: zero falhas/erros, JaCoCo ≥ 80% de linhas e nenhuma reserva conflitante aceita.

## Camadas

| Camada | Técnica | Ambiente |
|---|---|---|
| Integração HTTP | MockMvc com contexto Spring real, JDBC e segurança | H2 em memória e contas sintéticas |
| Regras | Serviço real, limites de intervalo, conflitos de recurso e usuário | JUnit 5 e AssertJ |
| Concorrência | 12 threads em barreira, exigir 1 sucesso e 11 conflitos | CountDownLatch e transações reais |
| Bootstrap e erros | Senha inicial, administrador existente, ausência de SQL | JUnit e Mockito |
| Carga HTTP | 200 consultas, concorrência 10 e 10 escritas disputadas | Node 22, servidor real e banco descartável |
| Documentação | Links relativos e blocos fechados com linguagem | verify-docs.mjs e leitura humana |
| Interface | Cadastro, login, CRUD, reserva e cancelamento | Inspeção visual humana pendente |

## Roteiro manual

1. Clone limpo: seguir somente README, compilar, iniciar com senha própria e entrar.
2. Admin cria gestor; gestor cria sala/equipamento; usuário não pode gerir recursos.
3. Usuário reserva no futuro; outro disputa o horário e recebe conflito.
4. Usuário tenta cancelar reserva alheia e recebe 403.
5. Titular cancela e o intervalo fica disponível.
6. Desativar recurso bloqueia novas reservas, preservando as existentes.
7. Reiniciar o JAR com H2 em arquivo e conferir dados persistidos.
8. Conferir desktop 1440px e mobile 390px: rótulos, foco, erros, diálogos e tabelas.

Marcar aprovação apenas após execução. A suíte não substitui inspeção visual, revisão por pares, restauração de backup ou auditoria de segurança.
