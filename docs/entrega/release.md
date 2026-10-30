# Procedimento de release candidate

Preparado, **não executado**. A decisão atual bloqueia a liberação formal. Somente publicação no GitHub foi solicitada, sem formulário.

## Pré-condições

1. Integrante lê todo o diff e registra revisão por pares.
2. Executar `./mvnw -B verify` antes de cada commit e corrigir bloqueadores.
3. Configurar SonarCloud e obter Quality Gate comprovado.
4. Após merge revisado, conferir CI verde na main e copiar SHA completo do commit avaliado.
5. Fazer clone novo, checkout desse SHA e seguir apenas README para compilar e iniciar.
6. Registrar teste visual, persistência por reinício, responsáveis por riscos e assinaturas.

## Tag imutável

Comandos de exemplo, somente depois das pré-condições. Substituir `SHA_AVALIADO` pelo hash real.

```bash
git fetch origin --tags
git show SHA_AVALIADO --no-patch
# Se rc1 já existir, escolher rc2. Nunca sobrescrever.
git tag -a v1.0.0-rc1 SHA_AVALIADO -m 'Release candidate 1: Organização de Recursos'
git push origin v1.0.0-rc1
```

Conferir workflow da tag, SHA, artifact e Quality Gate. Se houver correção, novo commit e novo candidato, sem mover tag.

## Associação das evidências

O índice pode citar CI da main desde que seja do commit exato tagueado. Preparar o índice final em alteração revisada e aguardar CI do commit final. Como um arquivo não pode conter o próprio SHA futuro, a associação final de execução/artifact/tag pode ficar na descrição do release do GitHub, identificando SHA e URLs imutáveis. O índice versionado deve explicar a associação e apontar para o release, sem afirmar resultados futuros.

Isso não autoriza usar evidência de commit anterior como se fosse do candidato. Baixar artifacts, conferir sourceSha e abrir todos os links antes do aceite.
