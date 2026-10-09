# Sistema de Informação Hospitalar (SIH)

Trabalho prático de **Programação Modular** — Engenharia de Software, PUC Minas.

Sistema para gerenciar pacientes, profissionais da saúde, consultas, internações, quartos e o histórico médico dos pacientes de um hospital, desenvolvido em Java com Spring Boot e arquitetura em camadas.

## Integrantes

- Guilherme Ferreira Valadares

## Tecnologias

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- H2 (banco em memória, usado durante o desenvolvimento)
- MySQL (perfil `mysql`, opcional)
- JUnit 5

## Como rodar

Pré-requisito: **JDK 21** instalado. Não precisa instalar o Maven, o projeto usa o Maven Wrapper (`mvnw`).

No Windows, dentro da pasta do projeto:

```bash
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Depois abra [http://localhost:8080](http://localhost:8080) no navegador para ver as telas.

No VS Code, com a extensão **Extension Pack for Java** (e de preferência a **Spring Boot Extension Pack**), também dá para abrir `SistemaHospitalarApplication.java` e clicar em **Run**.

### Rodar os testes

```bash
mvnw.cmd test
```

### Usar MySQL em vez do H2

Ajuste usuário e senha em `src/main/resources/application-mysql.properties` e rode:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Estrutura

```
src/main/java/br/pucminas/sih
├── model          classes do domínio (diagrama de classes) e regras de negócio
├── repository     acesso ao banco com Spring Data JPA
└── exception      exceções das regras de negócio
src/main/resources
├── static         telas do front-end (HTML e CSS, ainda sem funcionalidade)
└── application.properties
src/test/java      testes das regras de negócio
docs
├── diagrama-de-classes
└── cartoes-crc
```

As camadas `service` e `controller` (API REST) entram nas próximas etapas do trabalho.

## Regras de negócio implementadas no modelo

| Regra | Onde está |
|---|---|
| Um profissional não pode ter dois atendimentos no mesmo horário | `ProfissionalSaude.estaDisponivel()` e `adicionarConsulta()` |
| Consultas só dentro da disponibilidade cadastrada | `Disponibilidade.contem()` |
| Um quarto não pode ultrapassar a capacidade máxima | `Quarto.temVaga()` e `ocupar()` |
| A situação do quarto muda sozinha ao lotar ou liberar vaga | `Quarto.atualizarSituacao()` |
| Histórico de consultas e internações do paciente | `HistoricoMedico` |

## Documentação

- [Diagrama de classes](docs/diagrama-de-classes/diagrama-classes.png)
- [Cartões CRC](docs/cartoes-crc/Cartões%20CRC.pdf)
