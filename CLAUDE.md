# CLAUDE.md

## Projeto

Sistema de Informação Hospitalar (SIH), trabalho prático de Programação Modular da PUC Minas (Engenharia de Software). Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation), MySQL como banco padrão e H2 para desenvolvimento sem MySQL e para os testes.

A Sprint 1 entregou o modelo inicial (Pessoa, Paciente, ProfissionalSaude, Disponibilidade, Atendimento, Consulta, Internacao, Quarto, HistoricoMedico), os repositories, o diagrama de classes, os cartões CRC e as telas estáticas em `src/main/resources/static`.

## Sprint 2 — Implementação da API e Modelo

Enunciado completo em `docs/SPRINT2_TPPM.pdf`. Objetivo: evoluir o sistema implementando as classes de domínio, as regras de negócio e a API REST.

### Categorias de profissionais da saúde

Todos têm nome, registro profissional, telefone e e-mail; as atividades são diferentes.

- **Médico clínico geral**
  - Realiza consultas de rotina e avaliações iniciais.
  - Pode encaminhar pacientes para atendimento especializado.
  - Registra as informações das consultas realizadas.
- **Médico especialista**
  - Atua em uma área específica (cardiologia, neurologia, ortopedia...).
  - Realiza atendimentos da sua especialidade.
  - O sistema registra a especialidade e permite consultar os profissionais disponíveis por área de atuação.
- **Cirurgião**
  - Realiza procedimentos cirúrgicos.
  - Além das informações comuns aos médicos, possui informações da sua atuação cirúrgica.
  - O sistema registra os procedimentos realizados pelo profissional, com data, paciente e descrição.

### Modalidades de atendimento

Têm informações em comum, sem duplicar atributos e comportamentos (herança). Cada modalidade implementa a sua própria lógica de validação das condições para realizar o atendimento (polimorfismo).

- **Consulta de rotina**
  - Avaliação geral do paciente.
  - Data, horário, paciente, médico responsável e motivo.
  - Registro de observações após a realização.
- **Consulta especializada**
  - Atendimento de uma especialidade médica.
  - O profissional precisa ter área de atuação compatível com o atendimento solicitado.
  - Registro da especialidade e das observações.
- **Atendimento de emergência**
  - Avaliação imediata.
  - Registra a classificação de prioridade, por categorias fixas: baixa, moderada, alta, crítica.
  - Permite identificar os atendimentos que aguardam avaliação, os iniciados e os concluídos.
- **Procedimento cirúrgico**
  - Procedimentos realizados ou agendados.
  - Descrição, tipo de cirurgia, observações pré e pós-operatórias.
  - Associado a um paciente e a um cirurgião responsável.
  - Data e horário previstos e, quando aplicável, data e horário efetivos.
  - Situações: agendado, em andamento, concluído, cancelado.
  - Não pode ser concluído sem ter sido iniciado.

### Situação das consultas

- Situações: agendada, confirmada, em atendimento, concluída, cancelada, não compareceu.
- Identificar a situação atual e impedir transições incompatíveis (concluída não volta a agendada; cancelada não vira concluída sem novo agendamento).
- Representada por enum, nunca por texto livre.

### Entregas

1. Diagrama de classes atualizado (`docs/diagrama-de-classes`).
2. Classes de domínio com atributos, métodos e regras de negócio.
3. Endpoints REST: `/pacientes`, `/profissionais`, `/consultas`, `/internacoes`, `/quartos`.
4. Regras de negócio de categorias de profissionais, modalidades de atendimento, situações das consultas, procedimentos cirúrgicos e tipos de quartos.

## Arquitetura em camadas

Pacote base `br.pucminas.sih`:

- `controller` — endpoints REST. Recebe Request, chama o service, devolve Response. Sem regra de negócio.
- `service` — coordena a operação: busca entidades nos repositories, chama os métodos do model, salva. Controla as transações com `@Transactional`. Não contém regra de negócio.
- `repository` — interfaces Spring Data JPA.
- `model` — entidades e enums do domínio. **As regras de negócio ficam aqui**, em métodos das próprias entidades, e violações lançam `RegraNegocioException`.
- `dto` — records de entrada (`...Request`, com Bean Validation) e saída (`...Response`). Entidades nunca saem pela API.
- `exception` — exceções do domínio e o handler global (`@RestControllerAdvice`) que converte exceções em respostas HTTP.

## Estilo de código (obrigatório)

- Sem comentários no código.
- `if/else` explícito; nada de operador ternário nem one-liners que escondam a lógica.
- Nomes do domínio em português (`paciente`, `consulta`, `validar`, `agendar`) combinados com termos técnicos em inglês (`Request`, `Response`, `Service`, `Repository`, `Controller`).
- Enums persistidos sempre com `@Enumerated(EnumType.STRING)`.
- Seguir o estilo já existente: indentação com tab, construtor `protected` vazio para o JPA, construtor público com os campos obrigatórios, getters e só os setters necessários.

## Banco de dados

- Padrão: MySQL (`src/main/resources/application.properties`), usuário e senha por `DB_USER` e `DB_PASSWORD` (padrão `root`/`root`).
- Sem MySQL: perfil `h2` (`application-h2.properties`), com `./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2`.
- Testes: `src/test/resources/application.properties`, H2 em memória com `create-drop`; nunca dependem do MySQL.

## Regras de trabalho

- Nunca fazer commit nem push.
- Ao fim de cada etapa, rodar `./mvnw.cmd test` e corrigir até passar.
- Se algo do PDF não estiver claro, perguntar em vez de inventar.
- Terminar cada etapa com um resumo do que mudou e uma mensagem de commit sugerida.
