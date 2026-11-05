# Sistema de Informação Hospitalar (SIH)

Trabalho prático de **Programação Modular** — Engenharia de Software, PUC Minas.

Sistema para gerenciar pacientes, profissionais da saúde, consultas, atendimentos de emergência, procedimentos cirúrgicos, internações, quartos e o histórico médico dos pacientes de um hospital. Desenvolvido em Java com Spring Boot e arquitetura em camadas.

## Integrantes

- Guilherme Ferreira Valadares
- Vinicius Eduardo de Souza Matos Silva

## Tecnologias

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- MySQL (banco de dados da aplicação)
- H2 (banco em memória, usado nos testes e no perfil `h2`)
- JUnit 5

## Como rodar

Pré-requisitos: **JDK 21** e **MySQL 8**. Não precisa instalar o Maven, porque o projeto usa o Maven Wrapper (`mvnw`).

### Com MySQL

O banco `sih` é criado automaticamente na primeira execução, em `localhost:3306`. O usuário e a senha vêm das variáveis de ambiente `DB_USER` e `DB_PASSWORD`. Se elas não existirem, o padrão é `root` e `root`.

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="sua_senha"
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, use `./mvnw spring-boot:run`.

### Sem MySQL (perfil h2)

Para testar rapidamente, sem instalar nada além do JDK. Os dados ficam em memória e somem ao fechar a aplicação.

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

A aplicação sobe em [http://localhost:8080](http://localhost:8080).

### Testes

```bash
.\mvnw.cmd test
```

Os testes usam o H2 em memória e não precisam do MySQL.

## Arquitetura

```
src/main/java/br/pucminas/sih
├── controller     endpoints REST
├── service        coordena cada operação dentro de uma transação
├── repository     acesso ao banco com Spring Data JPA
├── model          classes de domínio e regras de negócio
├── dto            formato do JSON de entrada (Request) e de saída (Response)
├── exception      exceções e tratamento global de erros
└── config         carga de dados de exemplo no perfil h2 (CommandLineRunner)
src/main/resources
├── static                       telas do front-end (Sprint 1)
├── application.properties       configuração do MySQL
└── application-h2.properties    configuração do perfil h2
src/test                         testes de domínio, repositórios, serviços e validação
docs
├── SPRINT2_TPPM.pdf             enunciado da Sprint 2
├── api/requisicoes.http         exemplos de todas as requisições (REST Client)
├── diagrama-de-classes
└── cartoes-crc
```

Uma requisição segue o caminho `controller → service → model → repository`. As **regras de negócio ficam no `model`**: os services só buscam os objetos, chamam os métodos de domínio e controlam a transação com `@Transactional`. Os services devolvem DTOs, nunca as entidades JPA.

## Modelo de domínio

### Profissionais da saúde

`ProfissionalSaude` é abstrata e herda de `Pessoa` (nome, telefone e e-mail). Todo profissional tem registro profissional e horários de disponibilidade. Persistência com `SINGLE_TABLE`, usando a coluna `categoria`.

| Categoria | O que faz |
|---|---|
| `ClinicoGeral` | Realiza consultas de rotina e pode encaminhar pacientes para uma especialidade |
| `Especialista` | Tem uma `Especialidade` e realiza consultas especializadas da sua área |
| `Cirurgiao` | Tem uma `AreaCirurgica` e realiza procedimentos cirúrgicos, que ficam registrados na sua lista |

As diferenças entre as categorias são feitas por polimorfismo: métodos como `realizaConsultaDeRotina()`, `atuaEm(especialidade)`, `podeEncaminhar()` e `realizaProcedimentoCirurgico()` são sobrescritos em cada subclasse.

### Modalidades de atendimento

`Atendimento` é abstrata e guarda o que é comum a todas as modalidades (paciente, profissional e observações). Cada modalidade implementa o próprio **`validarRealizacao()`**, com as condições necessárias pra ser realizada.

| Modalidade | Validação própria |
|---|---|
| `ConsultaRotina` | O profissional precisa ser clínico geral, atender nesse dia e horário e não ter outra consulta no mesmo horário |
| `ConsultaEspecializada` | O profissional precisa ser especialista da mesma especialidade da consulta, com a mesma checagem de agenda |
| `AtendimentoEmergencia` | Não depende de agenda. Só pode ser iniciado se estiver aguardando avaliação |
| `ProcedimentoCirurgico` | O profissional precisa ser cirurgião e não pode ter outro procedimento no mesmo horário previsto |
| `Internacao` | O paciente não pode ter outra internação ativa, e o quarto precisa ter vaga e aceitar acompanhante, se pedido |

`ConsultaRotina` e `ConsultaEspecializada` herdam de `Consulta` (data, horário, motivo e situação), persistidas em `SINGLE_TABLE` com a coluna `modalidade`.

### Situações e transições

Todas as situações são enums persistidos pelo nome (`@Enumerated(EnumType.STRING)`), nunca como texto livre. As transições permitidas ficam dentro do próprio enum (`podeMudarPara` e `validarMudancaPara`), e qualquer mudança inválida lança `RegraNegocioException`.

**Consulta (`SituacaoConsulta`)**

| De | Pode ir para |
|---|---|
| AGENDADA | CONFIRMADA, CANCELADA, NAO_COMPARECEU |
| CONFIRMADA | EM_ATENDIMENTO, CANCELADA, NAO_COMPARECEU |
| EM_ATENDIMENTO | CONCLUIDA |
| CONCLUIDA, CANCELADA, NAO_COMPARECEU | situações finais (reagendar cria uma consulta nova) |

**Emergência (`SituacaoEmergencia`):** AGUARDANDO_AVALIACAO → EM_ATENDIMENTO → CONCLUIDO. A prioridade (`BAIXA`, `MODERADA`, `ALTA`, `CRITICA`) define a ordem da fila: primeiro a mais urgente e, em caso de empate, quem chegou antes.

**Procedimento cirúrgico (`SituacaoProcedimento`):** AGENDADO → EM_ANDAMENTO → CONCLUIDO, ou AGENDADO → CANCELADO. Não é possível concluir sem ter iniciado. O início e o fim registram a data e hora efetivas.

### Encaminhamento

Criado por `ProfissionalSaude.encaminhar(...)`. Só um clínico geral pode encaminhar, a partir de uma consulta de rotina **dele** que esteja **concluída**. Guarda o paciente, o clínico, a consulta de origem, a especialidade de destino, o motivo e a data.

### Tipos de quarto e cálculo da diária

`Quarto` é abstrato (`SINGLE_TABLE`, coluna `tipo`). Atributos comuns: número, andar, valor base da diária, ar-condicionado, banheiro privativo e situação. Cada tipo define a capacidade, se aceita acompanhante e como calcula a diária.

| Tipo | Capacidade | Acompanhante | Diária |
|---|---|---|---|
| `Enfermaria` | número de leitos | não | valor base com 5% de desconto por leito além do primeiro, até 20% |
| `Apartamento` | 1 paciente | só se tiver leito de acompanhante | valor base + adicional do leito (padrão R$ 40, elétrico R$ 90) + R$ 80 se tiver acompanhante |
| `Uti` | número de leitos | não | valor base + 50%, e + 20% se tiver isolamento |

A situação do quarto (`DISPONIVEL` ou `OCUPADO`) é atualizada automaticamente ao ocupar ou liberar uma vaga. O valor total da internação é a diária vezes os dias cobrados: até a alta prevista enquanto a internação está ativa, e até a alta efetiva depois da alta, com mínimo de 1 diária.

## Regras de negócio

| Regra | Onde está |
|---|---|
| CPF, registro profissional e número do quarto não se repetem | services (`existsBy...`) e colunas `UNIQUE` no banco |
| Consulta de rotina só com clínico geral | `ConsultaRotina.validarRealizacao()` |
| Consulta especializada só com especialista da mesma área | `ConsultaEspecializada.validarRealizacao()` e `Especialista.atuaEm()` |
| Profissional sem dois atendimentos no mesmo horário | `Consulta.validarHorarioNaAgenda()` e `ProfissionalSaude.possuiOutraConsultaNoHorario()` |
| Consulta só dentro da disponibilidade do profissional | `Disponibilidade.contem()` e `ProfissionalSaude.atendeNoHorario()` |
| Transições de situação da consulta | `SituacaoConsulta.podeMudarPara()` |
| Fila de emergência por prioridade e chegada | `AtendimentoEmergencia.ORDEM_DE_ATENDIMENTO` |
| Procedimento não conclui sem iniciar | `SituacaoProcedimento.podeMudarPara()` |
| Procedimento só com cirurgião e sem conflito de horário | `ProcedimentoCirurgico.validarRealizacao()` |
| Encaminhamento só de consulta de rotina concluída do próprio clínico | `ProfissionalSaude.encaminhar()` |
| Quarto não ultrapassa a capacidade | `Quarto.temVaga()` e `Internacao.validarRealizacao()` |
| Acompanhante só em quarto que permite | `Quarto.validarAcompanhante()` |
| Paciente sem duas internações ativas | `Paciente.adicionarInternacao()` |
| Histórico com todos os atendimentos do paciente | `HistoricoMedico` |

## Tratamento de erros

O `RestExceptionHandler` (`@RestControllerAdvice`) converte as exceções em respostas JSON com status, mensagem e detalhes:

| Situação | Status |
|---|---|
| Regra de negócio violada (`RegraNegocioException`) | 400 |
| Dados inválidos na requisição (Bean Validation) | 400 |
| JSON mal formatado ou parâmetro inválido | 400 |
| Recurso não encontrado (`RecursoNaoEncontradoException`) | 404 |
| Violação de restrição do banco | 409 |

## Endpoints

A API usa POST para criar (responde `201 Created` com o cabeçalho `Location`), GET para listar e buscar, e PATCH para as mudanças de situação (confirmar, iniciar, concluir, cancelar, dar alta etc.). Há exemplos prontos de todas as requisições, incluindo casos de erro, em [`docs/api/requisicoes.http`](docs/api/requisicoes.http) (extensão REST Client do VS Code).

### Pacientes

| Método | Rota | Descrição |
|---|---|---|
| POST | `/pacientes` | Cadastra um paciente |
| GET | `/pacientes` | Lista os pacientes |
| GET | `/pacientes?cpf=000.000.000-00` | Busca por CPF |
| GET | `/pacientes/{id}` | Busca por id |
| GET | `/pacientes/{id}/historico` | Histórico de atendimentos e encaminhamentos |

### Profissionais

| Método | Rota | Descrição |
|---|---|---|
| POST | `/profissionais/clinicos-gerais` | Cadastra um clínico geral |
| POST | `/profissionais/especialistas` | Cadastra um especialista |
| POST | `/profissionais/cirurgioes` | Cadastra um cirurgião |
| GET | `/profissionais` | Lista todos |
| GET | `/profissionais?especialidade=CARDIOLOGIA` | Lista especialistas de uma área |
| GET | `/profissionais?especialidade=CARDIOLOGIA&data=2026-11-11&horario=10:00` | Especialistas disponíveis nesse dia e horário |
| GET | `/profissionais/{id}` | Busca por id |

### Consultas

| Método | Rota | Descrição |
|---|---|---|
| POST | `/consultas/rotina` | Agenda uma consulta de rotina |
| POST | `/consultas/especializada` | Agenda uma consulta especializada |
| GET | `/consultas` | Lista as consultas |
| GET | `/consultas?data=2026-11-11` | Lista por data |
| GET | `/consultas/{id}` | Busca por id |
| PATCH | `/consultas/{id}/confirmar` | Confirma |
| PATCH | `/consultas/{id}/iniciar` | Inicia o atendimento |
| PATCH | `/consultas/{id}/concluir` | Conclui (com observações) |
| PATCH | `/consultas/{id}/cancelar` | Cancela |
| PATCH | `/consultas/{id}/nao-compareceu` | Registra não comparecimento |
| POST | `/consultas/{id}/encaminhamentos` | Encaminha a partir de uma consulta de rotina concluída |

### Emergências

| Método | Rota | Descrição |
|---|---|---|
| POST | `/emergencias` | Abre um atendimento de emergência |
| GET | `/emergencias?aguardando=true` | Fila de espera ordenada por prioridade e chegada |
| GET | `/emergencias?situacao=EM_ATENDIMENTO` | Lista por situação |
| GET | `/emergencias/{id}` | Busca por id |
| PATCH | `/emergencias/{id}/iniciar` | Inicia o atendimento |
| PATCH | `/emergencias/{id}/concluir` | Conclui (com observações) |

### Procedimentos cirúrgicos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/procedimentos` | Agenda um procedimento |
| GET | `/procedimentos?situacao=AGENDADO` | Lista por situação |
| GET | `/procedimentos?cirurgiaoId={id}` | Procedimentos realizados por um cirurgião |
| GET | `/procedimentos/{id}` | Busca por id |
| PATCH | `/procedimentos/{id}/iniciar` | Inicia (com data e hora de início) |
| PATCH | `/procedimentos/{id}/concluir` | Conclui (com data e hora de fim e observações) |
| PATCH | `/procedimentos/{id}/cancelar` | Cancela |

### Internações

| Método | Rota | Descrição |
|---|---|---|
| POST | `/internacoes` | Registra uma internação |
| GET | `/internacoes` | Lista as internações |
| GET | `/internacoes?ativas=true` | Lista só as ativas |
| GET | `/internacoes/{id}` | Busca por id |
| PATCH | `/internacoes/{id}/alta` | Dá alta (com a data da alta) |

### Quartos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/quartos/enfermarias` | Cadastra uma enfermaria |
| POST | `/quartos/apartamentos` | Cadastra um apartamento |
| POST | `/quartos/utis` | Cadastra uma UTI |
| GET | `/quartos` | Lista os quartos |
| GET | `/quartos?disponiveis=true` | Lista só os disponíveis |
| GET | `/quartos/{id}` | Busca por id |

Ao subir com o perfil `h2`, um `CommandLineRunner` cadastra dados de exemplo quando o banco está vazio (fica desligado nos testes e no MySQL).

## Documentação

- [Enunciado da Sprint 2](docs/SPRINT2_TPPM.pdf)
- [Diagrama de classes](docs/diagrama-de-classes/diagrama-classes.png)
- [Cartões CRC](docs/cartoes-crc/Cartões%20CRC.pdf)