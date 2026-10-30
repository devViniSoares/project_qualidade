# API REST

Base: `http://127.0.0.1:8080/api`. Corpo JSON. Datas ISO 8601 com deslocamento, como `2026-12-01T10:00:00-03:00`. Use datas futuras nas reservas.

## Autenticação e CSRF

Rotas de negócio exigem HTTP Basic. Para POST, PUT e DELETE, obtenha `GET /csrf`, mantenha o cookie de sessão e envie o token no cabeçalho retornado em `headerName`, normalmente `X-CSRF-TOKEN`. O cadastro público também exige CSRF. O frontend já implementa esse fluxo.

## Rotas

| Método e caminho | Permissão | Sucesso | Descrição |
|---|---|---|---|
| GET `/saude` | Público | 200 | status UP |
| GET `/csrf` | Público | 200 | headerName e token, cookie de sessão |
| POST `/cadastro` | Público com CSRF | 201 | Cria USUARIO; não aceita campo perfil |
| GET `/me` | Autenticado | 200 | id, nome, email, perfil |
| GET `/usuarios` | Admin | 200 | Contas sem senhas |
| POST `/usuarios` | Admin com CSRF | 201 | Cria conta com perfil escolhido |
| GET `/recursos?busca=texto&tipo=SALA` | Autenticado | 200 | Filtros opcionais: texto e SALA/EQUIPAMENTO |
| GET `/recursos/{id}` | Autenticado | 200 | Detalha recurso |
| POST `/recursos` | Gestor/Admin com CSRF | 201 | Cria recurso |
| PUT `/recursos/{id}` | Gestor/Admin com CSRF | 200 | Substitui campos editáveis |
| DELETE `/recursos/{id}` | Gestor/Admin com CSRF | 204 | Exclui somente sem histórico |
| GET `/recursos/{id}/disponibilidade?inicio=ISO&fim=ISO` | Autenticado | 200 | Booleano disponivel; não garante reserva |
| POST `/reservas` | Autenticado com CSRF | 201 | Valida e cria reserva |
| GET `/reservas?todas=false` | Autenticado | 200 | Próprias reservas |
| GET `/reservas?todas=true` | Gestor/Admin | 200 | Todas as reservas |
| POST `/reservas/{id}/cancelamento` | Titular/Gestão com CSRF | 200 | Idempotente, preserva histórico |

Logout: `POST /logout`, fora da base `/api`, com CSRF. O frontend descarta credenciais e recarrega. Não há recuperação ou troca de senha nesta versão.

## Payloads

Cadastro: nome até 100 caracteres, e-mail até 160, senha de 12 a 72 caracteres e no máximo 72 bytes UTF-8. Exemplo exclusivamente sintético:

```json
{"nome":"Pessoa de Teste","email":"pessoa@exemplo.test","senha":"SenhaApenasSintetica123!"}
```

Criação por administrador aceita também `perfil`: USUARIO, GESTOR ou ADMIN.

Recurso: nome e localização obrigatórios; descrição obrigatória podendo ser vazia; capacidade de 1 a 10000.

```json
{"nome":"Sala de projetos","tipo":"SALA","localizacao":"Bloco A","capacidade":12,"descricao":"Projetor e quadro","ativo":true}
```

Reserva: finalidade obrigatória até 300 caracteres, recursoId existente, intervalo futuro de até 12 horas. Cada reserva ocupa o recurso inteiro.

```json
{"recursoId":1,"inicio":"2026-12-01T10:00:00-03:00","fim":"2026-12-01T11:00:00-03:00","finalidade":"Reunião de projeto"}
```

Resposta:

```json
{"id":1,"recursoId":1,"recursoNome":"Sala de projetos","usuarioId":2,"usuarioNome":"Pessoa de Teste","inicio":"2026-12-01T10:00:00-03:00","fim":"2026-12-01T11:00:00-03:00","finalidade":"Reunião de projeto","status":"ATIVA"}
```

## Exemplo PowerShell

```powershell
$contaApi = Get-Credential -UserName 'admin@exemplo.test'
$basicApi = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($contaApi.UserName + ':' + $contaApi.GetNetworkCredential().Password))
$csrfApi = Invoke-RestMethod 'http://127.0.0.1:8080/api/csrf' -SessionVariable sessaoApi
$headersApi = @{ Authorization = "Basic $basicApi" }
$headersApi[$csrfApi.headerName] = $csrfApi.token
$recursoApi = @{nome='Sala de teste';tipo='SALA';localizacao='Bloco A';capacidade=10;descricao='Sintético';ativo=$true} | ConvertTo-Json
Invoke-RestMethod 'http://127.0.0.1:8080/api/recursos' -Method Post -WebSession $sessaoApi -Headers $headersApi -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($recursoApi))
```

## Erros

| Status | Motivo |
|---|---|
| 400 | Campo inválido, enum desconhecido, JSON incorreto, duração inválida ou data passada |
| 401 | Credenciais ausentes/inválidas |
| 403 | Perfil sem permissão, reserva alheia ou CSRF inválido/ausente |
| 404 | Recurso ou reserva inexistente |
| 409 | E-mail duplicado, conflito de agenda, recurso inativo ou exclusão com histórico |

Falhas de negócio usam Problem Details com status/detail, sem SQL ou stack trace. Erros gerados pelo filtro de segurança podem seguir formato diferente. Campos desconhecidos são rejeitados. Listas ainda não têm paginação. Use HTTPS para qualquer acesso em rede externa.
