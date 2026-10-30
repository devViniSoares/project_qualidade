# Índice de evidências

**Pacote preliminar, sem release candidate.** O CI da implementação passou na branch. Não há tag avaliada nem declaração de CI verde na main. Resultados locais iniciais não podem ser atribuídos retroativamente a uma tag.

## Evidências históricas da publicação anterior

Os resultados abaixo foram mantidos como histórico verificável e não comprovam CI do novo histórico incremental. O código foi republicado em commits menores a pedido do usuário. Nenhum novo PR foi aberto. Consulte também as execuções mais recentes da branch no GitHub Actions.

Commit anterior: [6a471e2ee1df570ac62a0f43dfe42b32166de99b](https://github.com/devViniSoares/project_qualidade/commit/6a471e2ee1df570ac62a0f43dfe42b32166de99b).

- [CI por push do SHA exato — aprovado](https://github.com/devViniSoares/project_qualidade/actions/runs/40989369102).
- [Artifact JaCoCo, JUnit, persistência e carga](https://github.com/devViniSoares/project_qualidade/actions/runs/40989369102/artifacts/12948770879), nome `evidencias-6a471e2ee1df570ac62a0f43dfe42b32166de99b`. Disponível até 28/01/2027, salvo exclusão; o download pode exigir login GitHub.
- [Carga do CI](../performance/resultado-ci-6a471e2.json): 200 respostas 200, p95 282,62 ms, zero erros, 1 criação e 9 conflitos. JSON transcrito diretamente da saída do job, com `sourceSha` conferido.
- [Persistência do CI](../testes/persistencia-ci-6a471e2.json): recurso, reserva e login preservados. JSON transcrito da saída do mesmo job.
- [Clone novo do SHA](../entrega/validacao-clone.md): verify, documentos e início/reinício do JAR aprovados.
- [RTM do commit](https://github.com/devViniSoares/project_qualidade/blob/6a471e2ee1df570ac62a0f43dfe42b32166de99b/docs/rtm.md), [defeitos](https://github.com/devViniSoares/project_qualidade/blob/6a471e2ee1df570ac62a0f43dfe42b32166de99b/docs/defeitos/README.md), [relatório de qualidade](https://github.com/devViniSoares/project_qualidade/blob/6a471e2ee1df570ac62a0f43dfe42b32166de99b/docs/relatorio-qualidade.md).

O job `sonar` foi **ignorado**, não aprovado. O [PR #1](https://github.com/devViniSoares/project_qualidade/pull/1) foi fechado a pedido do usuário. A publicação foi reorganizada em commits menores, sem abrir outro PR. O CI de pull request usa um commit temporário de merge; por isso os links acima usam a execução **push** do SHA da branch. Estes links avaliam a implementação 6a471e2, não uma tag nem futuros commits de documentação.

| Evidência | Caminho/acesso | Vínculo |
|---|---|---|
| Repositório | [GitHub](https://github.com/devViniSoares/project_qualidade) | Branch de prática |
| CI | [Execução exata](https://github.com/devViniSoares/project_qualidade/actions/runs/40989369102) | 6a471e2, sem RC |
| JaCoCo HTML/XML e JUnit | Artifact `evidencias-<SHA>` na execução | Nome identifica commit; abrir para conferir |
| Testes locais | [Relatório](../testes/relatorio.md) | Medição inicial sem commit |
| Carga | [Resultado](../performance/resultado-01.md), [JSON](../performance/resultado-01.json) | Local sem commit; CI produz sourceSha |
| Quality Gate SonarCloud | Pendente | Sem análise verificada |
| RTM | [Matriz](../rtm.md) | Versão desta branch |
| Defeitos | [Registro](../defeitos/README.md) | Versão desta branch |
| Qualidade | [Relatório](../relatorio-qualidade.md) | Parecer preliminar |
| Decisão | [Minuta](../decisao-qualidade.md) | Bloquear liberação formal, sem assinaturas |
| Checklist | [Entrega](../entrega/checklist.md) | Avaliação inicial e pendências |

Após revisão e merge, os links de RC precisam ser imutáveis: `/blob/<SHA>/...`, execução CI/artifact desse SHA e análise SonarCloud mostrando o mesmo commit. Exigir `sourceSha` da carga igual ao SHA da tag. Seguir [release](../entrega/release.md).

Não mover tag. Correções posteriores geram candidato novo, como `v1.0.0-rc2`. Não converter documentos preliminares em evidência positiva sem execução real.
