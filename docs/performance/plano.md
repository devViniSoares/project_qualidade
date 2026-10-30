# Plano de carga 01

## Objetivo

Medir disponibilidade autenticada e disputa de reserva pela API HTTP real em instância única local com H2 descartável. Não inferir capacidade de produção ou equivalência a 10.000 usuários.

## Procedimento

1. Compilar e iniciar JAR com `DB_URL=jdbc:h2:mem:carga;DB_CLOSE_DELAY=-1` e senha sintética de administrador.
2. Rodar `node scripts/carga.mjs` com a mesma `ADMIN_PASSWORD`; `BASE_URL` seleciona host/porta.
3. O script obtém CSRF/cookie, cria recurso sintético e aquece com 10 consultas.
4. Medir 200 consultas com 10 trabalhadores simultâneos.
5. Disputar o mesmo recurso/horário com 10 POSTs simultâneos.
6. Gravar JSON com horário, SHA, ambiente, status, p50, p95, máximo e vazão.
7. Encerrar a instância e descartar os dados. Nunca usar banco de uso real.

## Critérios anteriores à execução

- Consultas: p95 ≤ 1500 ms, 200 respostas HTTP 200 e zero erros inesperados.
- Disputa: exatamente 1 HTTP 201 e 9 HTTP 409. Conflitos esperados não são falhas.
- O script retorna código 1 ao violar critério, reprovando a etapa do CI.

Requisições incluem custo de BCrypt. Medição curta varia com CPU e ambiente e não detecta estabilidade prolongada ou vazamento de memória. O script deixa histórico no banco descartável.
