# Índice de evidências

**Pacote preliminar, sem release candidate.** Não há tag avaliada nem declaração de CI verde na main. Resultados locais iniciais foram gerados antes do primeiro commit e não podem ser atribuídos retroativamente a uma tag.

| Evidência | Caminho/acesso | Vínculo |
|---|---|---|
| Repositório | [GitHub](https://github.com/devViniSoares/project_qualidade) | Branch de prática |
| CI | [Execuções](https://github.com/devViniSoares/project_qualidade/actions/workflows/ci.yml) | Selecionar SHA proposto, ainda não é link de RC |
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
