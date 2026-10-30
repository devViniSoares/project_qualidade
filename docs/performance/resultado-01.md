# Resultado de carga 01 — local

Executado em **29/10/2026 às 22h46**, America/Sao_Paulo. Fonte: código de trabalho sem commit (`SEM_COMMIT` no [JSON bruto](resultado-01.json)). Não é evidência de tag RC.

| Medida | Resultado |
|---|---|
| Ambiente | Windows, Node v22.14.0, JDK 23.0.2, servidor local, H2 em memória |
| Aquecimento | 10 consultas |
| Consultas medidas | 200, concorrência 10 |
| Duração | 1851,48 ms |
| p50 | 88,99 ms |
| p95 | 110,96 ms |
| Máximo | 115,97 ms |
| Vazão | 108,02 requisições/s |
| Respostas | 200 HTTP 200, zero erros |
| Disputa | 10 tentativas, 1 HTTP 201, 9 HTTP 409 |
| Aceitação | Aprovado no [plano local](plano.md) |

O CI deve produzir novo JSON com `sourceSha` igual ao commit avaliado. O resultado local não comprova capacidade de produção ou estabilidade prolongada.
