# Decisão de qualidade

**Decisão atual: BLOQUEAR a liberação formal do release candidate.**

Data: 29/10/2026. Tag avaliada: **nenhuma**. Minuta técnica, sem assinatura/aceite atribuído a integrante. Publicar a branch para revisão não aprova a entrega ou uso em produção.

## Critérios

| Critério | Evidência | Resultado |
|---|---|---|
| Build e testes | [CI 6a471e2](https://github.com/devViniSoares/project_qualidade/actions/runs/40989369102), 24 testes e [clone novo](entrega/validacao-clone.md) | Aprovado na branch; falta main revisada |
| Cobertura ≥ 80% | JaCoCo inicial: 139/140 linhas | Aprovado localmente |
| Reservas sem conflito numa instância | 12 threads e [carga](performance/resultado-01.md) | Aprovado no ambiente medido |
| CI verde na main do commit da tag | Sem tag avaliada | Não atendido |
| Quality Gate SonarCloud | Configuração externa pendente | Não atendido |
| Revisão por pares | Sem PR aberto; revisão humana ainda pendente | Não atendido |
| Persistência por reinício | [Teste real](testes/persistencia-local.json) | Aprovado localmente após corrigir gravação adiada |
| Usabilidade | [Plano](testes/plano.md) | Inspeção visual pendente |
| Assinaturas e responsáveis | Nomes e declarações não fornecidos | Não atendido |

## Riscos

**Nenhum risco foi aceito nesta minuta.** Permanecem abertos, sem presumir aceite humano.

| Risco | Impacto | Justificativa de eventual aceite | Responsável | Estado |
|---|---|---|---|---|
| Uma JVM | Conflitos em adaptação distribuída | Somente demonstração de instância única | A designar pelo grupo | Não aceito |
| HTTP sem rate limiting | Exposição se usado fora de localhost | Somente ambiente local restrito | A designar pelo grupo | Não aceito |
| Teste visual ausente | Defeitos de uso/layout não detectados | Depende de execução manual e aceite | A designar pelo grupo | Não aceito |
| Listas sem paginação | Degradação com alto volume | Somente volume limitado comprovado | A designar pelo grupo | Não aceito |

## Integrantes

Nomes completos e contribuições não informados. Até quatro integrantes devem registrar suas próprias declarações após revisão do diff e execução da suíte. Não foi fabricada autoria humana, assinatura ou revisão por pares.

## Publicação

[Repositório público](https://github.com/devViniSoares/project_qualidade). O usuário solicitou **somente GitHub, sem formulário**. Nenhum envio foi realizado. Mudar para liberar ou liberar com ressalvas exige evidências do commit da tag e responsáveis por riscos aceitos.
