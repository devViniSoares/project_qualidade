# Validação em clone novo

Data: **29/10/2026**, aproximadamente 23h03–23h04, America/Sao_Paulo.

Repositório público clonado novamente em `validacao-clone-limpo/`, diretório novo, sem copiar banco, target, configurações ou fontes da pasta de desenvolvimento. Foi usado o Maven Wrapper e as instruções do README. O cache de dependências Maven do sistema permaneceu disponível; não se afirma máquina virtual limpa ou cache de dependências vazio.

Commit avaliado: `6a471e2ee1df570ac62a0f43dfe42b32166de99b`, branch `pratica/entrega-final`. O clone da branch é necessário enquanto o PR aguarda revisão e merge.

```powershell
git clone --branch pratica/entrega-final --single-branch https://github.com/devViniSoares/project_qualidade.git validacao-clone-limpo
Set-Location validacao-clone-limpo
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-23'
.\mvnw.cmd -B verify
node scripts/verify-docs.mjs
node scripts/persistencia.mjs
```

| Verificação | Resultado |
|---|---|
| Build sem arquivos copiados | BUILD SUCCESS, 24 testes, zero falhas/erros |
| Gate JaCoCo | Aprovado, 139/140 linhas e 48/50 branches |
| Documentos | 16 arquivos com links locais/blocos válidos |
| JAR, bootstrap e saúde | Aplicação iniciou em banco novo com senha sintética gerada pelo teste |
| API real | Criação de recurso e reserva concluídas antes do reinício |
| Persistência | Recurso, reserva e login preservados, sem nova senha de bootstrap |
| Código de origem | `sourceSha` do teste igual a 6a471e2 |
| Inspeção visual | Não realizada: ferramenta de navegador indisponível |

Resultado bruto do teste de JAR: [clone-limpo.json](../testes/clone-limpo.json). O teste modifica apenas seu relatório local e dados descartáveis, sem alterar a origem remota. Este resultado não substitui revisão por pares ou Quality Gate SonarCloud.
