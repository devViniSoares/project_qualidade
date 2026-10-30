# Checklist de entrega

## Avaliação inicial, sem correções

Em 24/10/2026 foi feito clone novo de `https://github.com/devViniSoares/project_qualidade`, sem aplicação ou banco reaproveitado. O remoto estava vazio, sem commits. Não havia README, Wrapper, aplicação ou evidências; seguir o README e executar `./mvnw -B verify` era impossível. Nenhuma correção foi feita antes dessa constatação.

A referência CineSenac foi clonada separadamente no commit `f8090eee219694b3223257d077e8b26403b9a3bd`. Ela usa Python e não representa a aplicação Java do grupo. O usuário confirmou Organização de Recursos. A implementação ocorreu na branch `pratica/entrega-final`.

## Após correções — 29/10/2026

| Item | Existe | Atualizado | Caminho/evidência |
|---|---|---|---|
| Repositório público | Sim | Sim | [GitHub](https://github.com/devViniSoares/project_qualidade) |
| Revisão por pares | Pendente | PR aberto | [PR #1](https://github.com/devViniSoares/project_qualidade/pull/1), exige revisão humana |
| Aplicação Java | Sim | Teste local aprovado | [pom.xml](../../pom.xml), [código](../../src/main/java/br/edu/senac/recursos/) |
| README reproduzível | Sim | Clone novo 6a471e2 aprovado | [README](../../README.md), [validação](validacao-clone.md) |
| API documentada | Sim | Sim | [Contrato](../api.md) |
| RTM | Sim | Sim | [Matriz](../rtm.md), [requisitos](../requisitos.md) |
| Contexto, componentes e sequências | Sim | Sim | [Diagramas](../arquitetura.md) |
| Plano e relatório de testes | Sim | Execução local | [Plano](../testes/plano.md), [relatório](../testes/relatorio.md) |
| Pipeline CI | Sim | Verificações aprovadas na branch, Sonar ignorado | [Execução](https://github.com/devViniSoares/project_qualidade/actions/runs/40989369102), [workflow](../../.github/workflows/ci.yml) |
| JaCoCo | Sim | 99,29% local inicial | [Relatório](../testes/relatorio.md), artifact do CI por SHA |
| SonarCloud | Parcial | Configuração externa pendente | Job sonar, variables e secret necessários |
| Plano e resultado de carga | Sim | Execução local | [Plano](../performance/plano.md), [resultado](../performance/resultado-01.md) |
| Defeitos | Sim | Sim | [Registro](../defeitos/README.md) |
| Relatório de qualidade | Sim | Preliminar | [Relatório](../relatorio-qualidade.md) |
| Índice de evidências | Sim | Parcial, sem RC | [Índice](../evidencias/README.md) |
| Decisão assinada | Documento presente | Assinaturas não fornecidas | [Minuta](../decisao-qualidade.md) |
| Tag RC com CI verde na main | Não | Bloqueada por pré-requisitos | [Procedimento](release.md) |
| Formulário | Não | Fora do escopo por pedido do usuário | Nenhum envio deve ser realizado |

## Bloqueadores por prioridade

1. Revisão humana do diff e aceite das premissas de requisitos.
2. CI e artifacts da main após merge revisado; a branch 6a471e2 já passou no CI.
3. Configuração e Quality Gate SonarCloud comprovado no mesmo SHA.
4. Navegação visual e verificações pendentes do plano de testes.
5. Tag anotada somente após os critérios, sem mover tags existentes.
6. Evidências imutáveis e assinaturas com responsáveis identificados.

O formulário não integra esta execução: o usuário determinou apenas publicação no GitHub.
