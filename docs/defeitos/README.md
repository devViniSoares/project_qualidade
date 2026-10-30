# Registro de defeitos e pendências

Data: 29/10/2026. Versão em desenvolvimento, sem RC.

| ID | Severidade | Descrição e impacto | Estado | Evidência/ação |
|---|---|---|---|---|
| D01 | Bloqueador inicial | Repositório vazio, aplicação não reproduzível | Corrigido na branch | Java, Wrapper e README |
| D02 | Bloqueador de entrega | SonarCloud sem Quality Gate comprovado | Aberto | Configurar variables/secret e anexar análise do SHA |
| D03 | Bloqueador de entrega | Revisão por pares e aceite de requisitos ausentes | Aberto | Revisão humana do PR |
| D04 | Alto para uso distribuído | Exclusão de reservas garantida apenas numa JVM | Limitação documentada | Monitor cobre commit, teste de 12 threads; falta garantia distribuída |
| D05 | Alto para internet | HTTP local sem HTTPS e rate limiting | Limitação documentada | Escuta 127.0.0.1; não expor como produção |
| D06 | Médio | Navegação visual não executada, host indisponível | Aberto | Roteiro manual desktop/mobile |
| D07 | Médio | Listas sem paginação | Aberto | Limitar ao volume acadêmico validado |
| D08 | Médio | Reinício com banco em arquivo não registrado inicialmente | Corrigido | [Teste do JAR](../testes/persistencia-local.json) preservou recurso, reserva e login |
| D09 | Baixo, ambiente | PowerShell interpretou argumento Maven com pontos como fase | Corrigido | Argumento entre aspas; Wrapper e verify aprovados |
| D10 | Alto | H2 com gravação adiada perdeu a última reserva após encerramento abrupto | Corrigido | WRITE_DELAY=0 no banco padrão e no teste, repetição aprovada |
| D11 | Alto | Representação textual automática dos payloads poderia revelar senha em logs DEBUG | Corrigido | toString redigido e teste de regressão |

Nenhum risco foi aceito em nome de integrante não identificado. O grupo deve indicar responsável e justificativa para qualquer aceite. Documentar D04/D05/D07 não equivale à correção técnica.
