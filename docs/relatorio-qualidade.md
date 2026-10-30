# Relatório de qualidade

## Escopo

Organização de Recursos em Java, publicação apenas no GitHub. Os [requisitos](requisitos.md) são premissas aguardando aceite do grupo. Não foram inventadas assinaturas ou revisões.

Execução local em 29/10/2026: verify aprovado, 24 testes, 99,29% de linhas e 96% de branches JaCoCo. Carga: 200 consultas, p95 110,96 ms, zero erros; uma reserva e nove conflitos na disputa. Fontes: [testes](testes/relatorio.md), [carga](performance/resultado-01.md) e [JSON](performance/resultado-01.json).

## Avaliação

| Atributo | Evidência | Limite |
|---|---|---|
| Correção funcional | RTM, testes de disponibilidade e cancelamento | Premissas requerem aceite humano |
| Segurança | Autorização backend, CSRF, BCrypt, ausência de senha nas respostas | Sem auditoria ou controles completos de internet |
| Confiabilidade | Transação e monitor, 12 threads, histórico preservado e reinício do JAR aprovado | Garantia limitada a instância única e ao ensaio registrado |
| Desempenho | Carga local abaixo do p95 de 1500 ms | Amostra curta, sem extrapolar para produção |
| Manutenibilidade | Componentes, testes e JaCoCo | SonarCloud externo pendente |
| Usabilidade | Interface em português, rótulos e responsividade implementados | Inspeção visual e acessibilidade pendentes |
| Reprodutibilidade | Wrapper, H2, CI Java 21 e clone novo aprovados no SHA 6a471e2 | Falta CI da main após merge revisado |

## Parecer

Evidência favorável à demonstração acadêmica local de instância única. **Liberação formal bloqueada** até revisão por pares, CI da main, SonarCloud e fechamento das verificações pendentes. Não há base para declarar pronto para produção.

[Defeitos](defeitos/README.md) e [decisão](decisao-qualidade.md) registram pendências. Resultados locais sem commit não devem ser atribuídos a uma tag futura.

Validação adicional do commit 6a471e2: [CI aprovado](https://github.com/devViniSoares/project_qualidade/actions/runs/40989369102), [clone novo](entrega/validacao-clone.md) e [carga Ubuntu/Java 21](performance/resultado-ci-6a471e2.json), p95 282,62 ms e zero erros. Sonar ignorado e revisão humana ausente mantêm a liberação formal bloqueada.
