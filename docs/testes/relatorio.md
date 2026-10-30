# Relatório de testes — execução local final

Data: **29/10/2026**, America/Sao_Paulo. Código sem commit durante a medição inicial; não é evidência de uma tag RC.

| Item | Resultado |
|---|---|
| Ambiente | Windows, Oracle JDK 23.0.2, Maven Wrapper 3.9.11, H2 em memória |
| Comando | `.\mvnw.cmd -B verify` |
| Build | BUILD SUCCESS |
| Casos | 24: 21 de RecursosTest e 3 de BootstrapTest |
| Falhas/erros/ignorados | 0 / 0 / 0 |
| JaCoCo linhas | 139 cobertas, 1 não coberta: **99,29%** |
| JaCoCo branches | 48 cobertos, 2 não cobertos: **96,00%** |
| Gate | 80% de linhas, aprovado |
| JAR | `target/organizacao-recursos-1.0.0-SNAPSHOT.jar` |
| JavaScript | `node --check src/main/resources/static/app.js`, aprovado |
| Concorrência | 12 chamadas ao serviço, exatamente 1 reserva |
| Interface real | Não verificada: host de navegação e pipe de automação indisponíveis |

Relatórios: `target/surefire-reports/TEST-*.xml`, `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.xml`. O CI publica artifacts por SHA; a associação final deve constar no [índice](../evidencias/README.md).

## Persistência após reinício

Às 23h01 de 29/10/2026, o [teste de duas execuções do JAR](persistencia-local.json) confirmou recurso, reserva e login preservados, reiniciando sem senha de bootstrap. O primeiro ensaio detectou perda da reserva ao interromper imediatamente o processo com WRITE_DELAY padrão do H2. A configuração foi corrigida para WRITE_DELAY=0 e a repetição passou. Script reproduzível: `node scripts/persistencia.mjs`, porta 8082 livre.

Sem teste de penetração, múltiplas instâncias, acessibilidade assistiva ou escala de produção. O relatório final exige CI, revisão humana e SonarCloud.
