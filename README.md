# Organização de Recursos

Aplicação Java para organizar **salas e equipamentos** e reservar horários sem conflitos. Projeto acadêmico de Qualidade de Software 2026.2 — Senac.

## Funcionalidades

- Cadastro e login com BCrypt e perfis Usuário, Gestor e Administrador.
- Catálogo com busca e filtro por tipo; gestão de recursos pela equipe.
- Disponibilidade, reservas futuras de até 12 horas e cancelamento com histórico.
- Bloqueio de sobreposição de recurso e dos compromissos do próprio usuário.
- Interface responsiva em português e API REST com proteção CSRF.

O [CineSenac](https://github.com/afonsolelis/cinesenac) serviu como referência documental. O usuário confirmou o domínio Organização de Recursos; os [requisitos locais](docs/requisitos.md) são premissas para validação pelo grupo.

## Pré-requisitos

- JDK **21 ou 23**, `JAVA_HOME` apontando ao JDK e `java` no PATH. O CI usa Java 21.
- Internet no primeiro build: o Wrapper baixa Maven 3.9.11 e dependências oficiais. Não é necessário instalar Maven.
- Node.js **22** para validar documentos e executar carga, sem pacotes extras.
- Porta 8080 livre. Não é necessário Docker nem banco externo.

## Clone limpo e testes

Linux/macOS/Git Bash:

```bash
git clone https://github.com/devViniSoares/project_qualidade.git
cd project_qualidade
./mvnw -B verify
node scripts/verify-docs.mjs
```

Windows PowerShell:

```powershell
git clone https://github.com/devViniSoares/project_qualidade.git
Set-Location project_qualidade
# Ajuste ao local do seu JDK, se necessário:
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-23'
.\mvnw.cmd -B verify
node scripts/verify-docs.mjs
```

Enquanto a implementação estiver em revisão, faça `git switch pratica/entrega-final` após clonar. Depois do merge, a main conterá a aplicação.

O `verify` executa testes, gera o JAR e `target/site/jacoco/index.html`. O build falha abaixo de **80% de linhas cobertas**, sem excluir classes de negócio. Testes usam H2 em memória e contas sintéticas.

## Executar

O primeiro início exige uma senha de administrador de 12 a 72 caracteres e até 72 bytes UTF-8. Defina uma senha própria no terminal, sem gravá-la no repositório. O e-mail padrão é `admin@exemplo.test`.

Linux/macOS/Git Bash:

```bash
read -rsp 'Senha inicial do administrador: ' ADMIN_PASSWORD; echo
export ADMIN_PASSWORD
java -jar target/organizacao-recursos-1.0.0-SNAPSHOT.jar
```

Windows PowerShell:

```powershell
$credencialLocal = Get-Credential -UserName 'admin@exemplo.test' -Message 'Defina uma senha inicial de pelo menos 12 caracteres'
$env:ADMIN_PASSWORD = $credencialLocal.GetNetworkCredential().Password
java -jar target/organizacao-recursos-1.0.0-SNAPSHOT.jar
```

Abra **http://127.0.0.1:8080**, entre como administrador e cadastre recursos e gestores. Usuários podem criar conta pela própria tela. O banco começa sem recursos; não há senha padrão de demonstração.

Os dados persistem em `data/recursos.mv.db`. Reiniciar mantém os registros. Se já existir administrador, a variável de bootstrap não altera sua senha. Faça backup com a aplicação parada. Não abra o arquivo em várias instâncias.

| Variável | Padrão | Uso |
|---|---|---|
| `ADMIN_EMAIL` | `admin@exemplo.test` | Primeiro administrador |
| `ADMIN_PASSWORD` | vazio | Exigida somente na criação inicial |
| `DB_URL` | `jdbc:h2:file:./data/recursos;DB_CLOSE_ON_EXIT=FALSE;WRITE_DELAY=0` | H2 persistente ou descartável |
| `DB_PASSWORD` | vazio | Senha do banco local |
| `PORT` | `8080` | Porta HTTP |
| `BIND_ADDRESS` | `127.0.0.1` | Interface de escuta |

O protótipo usa HTTP Basic e CSRF, com credenciais somente em memória na interface. Destina-se a demonstração em localhost. HTTPS, limitação de tentativas e controles de implantação são necessários antes de exposição pública. A garantia de exclusão de reservas vale para **uma instância**.

## Documentação

- [API](docs/api.md) · [Requisitos](docs/requisitos.md) · [RTM](docs/rtm.md)
- [Arquitetura e diagramas](docs/arquitetura.md)
- [Plano de testes](docs/testes/plano.md) · [Relatório](docs/testes/relatorio.md)
- [Plano de carga](docs/performance/plano.md) · [Resultado](docs/performance/resultado-01.md)
- [Defeitos](docs/defeitos/README.md) · [Relatório de qualidade](docs/relatorio-qualidade.md)
- [Checklist](docs/entrega/checklist.md) · [Evidências](docs/evidencias/README.md)
- [Decisão de qualidade](docs/decisao-qualidade.md) · [Release](docs/entrega/release.md) · [Changelog](CHANGELOG.md)

## Carga reproduzível

Use banco **descartável**, nunca dados reais. O script cria recursos e reservas sintéticos. No PowerShell:

```powershell
$env:ADMIN_PASSWORD = [guid]::NewGuid().ToString('N')
$env:DB_URL = 'jdbc:h2:mem:carga;DB_CLOSE_DELAY=-1'
$env:PORT = '8081'
$processoCarga = Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" -ArgumentList '-jar','target/organizacao-recursos-1.0.0-SNAPSHOT.jar' -WindowStyle Hidden -PassThru
# Aguarde http://127.0.0.1:8081/api/saude responder.
$env:BASE_URL = 'http://127.0.0.1:8081'
node scripts/carga.mjs
Stop-Process -Id $processoCarga.Id
Remove-Item Env:ADMIN_PASSWORD,Env:DB_URL,Env:PORT,Env:BASE_URL
```

O JSON fica em `performance-local/resultado.json`. O CI executa o mesmo cenário e publica o resultado com o SHA.

Persistência por reinício: após compilar, execute `node scripts/persistencia.mjs` com porta 8082 livre (ou defina `TEST_PORT`). O teste inicia duas vezes o JAR, usa banco novo em `.tools/`, encerra somente o processo que criou e verifica recurso, reserva e login. O resultado fica em `docs/testes/persistencia-local.json`. O banco padrão usa `WRITE_DELAY=0` para desativar gravação adiada do H2; mantenha essa opção ao configurar outro H2 em arquivo.

## CI e SonarCloud

O [workflow](.github/workflows/ci.yml) verifica Java 21, documentação e carga. Artifacts `evidencias-<SHA>` contêm JaCoCo HTML/XML, JUnit e JSON de carga.

Para SonarCloud, um mantenedor deve importar o repositório no serviço, definir as variables `SONAR_PROJECT_KEY` e `SONAR_ORGANIZATION` e o secret `SONAR_TOKEN` no GitHub. Não coloque tokens no código ou em prompts. O job Sonar fica **ignorado sem as variables**; quando configurado, exige token e aguarda o Quality Gate. Testes verdes com Sonar ignorado **não comprovam aprovação do Quality Gate**.

Nenhuma tag RC deve ser criada antes de revisão por pares, CI verde na main e evidências do mesmo commit. O usuário solicitou apenas publicação no GitHub, sem envio de formulário.

## Estrutura

```text
src/main/java/br/edu/senac/recursos/   API, segurança e regras
src/main/resources/                  interface, configuração e schema
src/test/                            testes de integração e concorrência
scripts/                             documentação e carga HTTP
.github/workflows/ci.yml              pipeline Java 21
docs/                                especificação e entrega
```
