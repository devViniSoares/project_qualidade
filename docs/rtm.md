# Matriz de rastreabilidade

IDs definidos em [requisitos](requisitos.md). Testes em [RecursosTest.java](../src/test/java/br/edu/senac/recursos/RecursosTest.java). Os resultados abaixo são locais e não comprovam Quality Gate externo.

| Requisito | Implementação | Teste/evidência | Situação |
|---|---|---|---|
| RF01, RNF01 | [Seguranca](../src/main/java/br/edu/senac/recursos/Seguranca.java), [Usuarios](../src/main/java/br/edu/senac/recursos/Usuarios.java) | acessoPublicoESeguranca, csrfObrigatorio, usuarioNaoAlteraRecursos, senhasPersistidasComHash | Passou localmente |
| RF02 | Usuarios e Api | cadastroNaoPermiteEscaladaNemDuplicata, validacaoCadastro | Passou localmente |
| RF03, RN04, RN05 | [Agenda](../src/main/java/br/edu/senac/recursos/Agenda.java) | gestorGerenciaRecursos, validaRecursoEParametros, bloqueiaSobreposicaoEConservaHistorico | Passou localmente |
| RF04 | Agenda e Api | gestorGerenciaRecursos | Passou localmente |
| RF05 | Agenda.disponivel | disponibilidadeEReservaComCancelamento | Passou localmente |
| RF06, RN01 | Agenda.reservar | bloqueiaSobreposicaoEConservaHistorico, concorrenciaNaoDuplicaReserva | Passou localmente |
| RN02 | Agenda.reservar | bloqueiaConflitoDoUsuarioEPermiteIntervaloAdjacente | Passou localmente |
| RN03 | Agenda.validarIntervalo | validaDuracaoDaReserva, naoReservaNoPassadoNemSemDatas | Passou localmente |
| RF07 | Agenda.reservas | privacidadeEPermissoesDeCancelamento | Passou localmente |
| RF08, RN06 | Agenda.cancelar | disponibilidadeEReservaComCancelamento, privacidadeEPermissoesDeCancelamento | Passou localmente |
| RF09 | Api.usuarios e novoUsuario | somenteAdminGerenciaUsuarios | Passou localmente |
| RF10, RNF06 | [Interface](../src/main/resources/static/index.html), [JavaScript](../src/main/resources/static/app.js) | GET / e sintaxe JS verificados; navegação visual indisponível | Parcial |
| RN07 | Agenda.reservar | concorrenciaNaoDuplicaReserva: reserva ocupa todo o recurso | Passou localmente |
| RNF02 | Monitor e TransactionTemplate | concorrenciaNaoDuplicaReserva: 12 disputas, 1 sucesso | Passou localmente |
| RNF03 | [pom.xml](../pom.xml), [CI](../.github/workflows/ci.yml) | verify e 24 testes, gate JaCoCo | Local aprovado; CI deve ser consultado |
| RNF04 | [Script de carga](../scripts/carga.mjs) | [Resultado 01](performance/resultado-01.md) | Passou no ambiente local |
| RNF05 | [Schema](../src/main/resources/schema.sql), Agenda | Integridade e histórico na suíte; [reinício real](testes/persistencia-local.json) preservou recurso, reserva e login | Passou localmente |

[BootstrapTest](../src/test/java/br/edu/senac/recursos/BootstrapTest.java) também valida senha inicial obrigatória, reutilização de administrador e ausência de SQL nas respostas de erro.
