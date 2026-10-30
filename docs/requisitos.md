# Requisitos — Organização de Recursos

## Origem e escopo

O usuário confirmou Organização de Recursos, implementação Java e publicação em `devViniSoares/project_qualidade`. O roteiro da Aula 14 define os entregáveis. Como não foi fornecida uma especificação funcional própria, as regras abaixo são **premissas de implementação para validação pelo grupo**, inspiradas na organização documental do CineSenac. Não equivalem à migração dos seus 30 RFs de cinema.

## Personas

| Perfil | Objetivo | Permissão |
|---|---|---|
| Usuário | Encontrar recurso e reservar uma atividade | Catálogo, disponibilidade, próprias reservas e cancelamento próprio |
| Gestor | Administrar espaços e equipamentos | Permissões de usuário, gestão de recursos, consulta/cancelamento de todas as reservas |
| Administrador | Administrar o grupo e o catálogo | Permissões do gestor e criação/listagem de contas com perfis |

## Requisitos funcionais

| ID | Comportamento verificável |
|---|---|
| RF01 | Autenticar e-mail e senha e restringir operações por perfil |
| RF02 | Cadastrar usuário com e-mail único normalizado, senha protegida e perfil fixo USUARIO |
| RF03 | Criar, editar, desativar e excluir recurso sem histórico, com nome, tipo, localização, capacidade e descrição |
| RF04 | Listar, detalhar e buscar recursos por nome/localização e filtrar SALA/EQUIPAMENTO |
| RF05 | Consultar disponibilidade de um recurso em intervalo com fuso horário |
| RF06 | Criar reserva futura com finalidade, validando recurso ativo, duração e conflitos |
| RF07 | Listar próprias reservas sem revelar reservas de outros usuários |
| RF08 | Cancelar reserva própria, ou qualquer reserva se gestor/admin, liberando o intervalo e preservando histórico |
| RF09 | Permitir que somente administrador crie e liste contas com perfis |
| RF10 | Oferecer interface em português para autenticação, catálogo, reserva e gestão |

## Regras de negócio

| ID | Regra |
|---|---|
| RN01 | Um recurso não pode ter duas reservas ATIVAS sobrepostas. Intervalos são semiabertos: [início, fim), permitindo horários adjacentes |
| RN02 | Um usuário não pode possuir reservas ATIVAS sobrepostas, mesmo em recursos diferentes |
| RN03 | Início no futuro, fim posterior ao início e duração máxima de 12 horas |
| RN04 | Recurso inativo não recebe novas reservas. Desativar não cancela automaticamente reservas existentes |
| RN05 | Recurso com qualquer histórico de reserva não pode ser excluído. Deve ser desativado |
| RN06 | Cancelamento é idempotente e não apaga o registro |
| RN07 | Capacidade representa informação descritiva. Cada reserva ocupa o recurso inteiro, sem cotas ou assentos individuais |

## Requisitos não funcionais

| ID | Critério |
|---|---|
| RNF01 | Autorização no servidor, CSRF nas mutações, BCrypt, validação e ausência de senha nas respostas |
| RNF02 | Em disputa de 12 chamadas simultâneas no serviço, somente uma reserva vence; garantia em instância única |
| RNF03 | Maven Wrapper reproduzível, testes sem banco externo e gate mínimo de 80% de linhas JaCoCo |
| RNF04 | Carga de 200 consultas autenticadas, concorrência 10, p95 ≤ 1500 ms e 0 erros inesperados, no ambiente medido |
| RNF05 | Dados persistentes em H2 local, histórico de cancelamentos e integridade referencial |
| RNF06 | Interface responsiva, rótulos de campos, foco visível e mensagens de estado; inspeção visual humana pendente |

## Fora do escopo

Anexos, recorrência, aprovação de solicitações, quotas por recurso, calendário externo, notificações, implantação distribuída, recuperação de senha e escala de produção. Não foi executado teste de 10.000 usuários. As metas do CineSenac não são resultados deste sistema.
