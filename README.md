# clinica-api

API REST para uma clínica marcar consultas: cadastro de médicos e pacientes e uma agenda que aplica as regras de funcionamento da clínica antes de aceitar um horário.

O projeto nasceu dos meus estudos de Spring Boot (um CRUD de médicos e pacientes com Flyway e MySQL, uma API de produtos e alguns exercícios menores). Juntei o que fazia sentido num projeto só, reescrevi o código do zero e acrescentei o que faltava para ele se comportar como um sistema de verdade: regras de agendamento, autenticação, testes contra um banco real e um jeito simples de subir tudo com Docker.

## Tecnologias

- Java 25 e Spring Boot 4.1.2 (Web MVC, Data JPA, Validation, Security com OAuth2 Resource Server)
- PostgreSQL 18 com migrations do Flyway
- springdoc-openapi 3.2.0 (Swagger UI)
- JUnit 5, Mockito e Testcontainers
- Maven Wrapper, Docker e GitHub Actions

## Regras de agendamento

Todas ficam em `consulta/validacao`, uma classe por regra, e são executadas antes de gravar a consulta.

| Regra | Resposta quando não é atendida |
|---|---|
| A clínica funciona de segunda a sábado, das 07:00 às 19:00 | 422 |
| Consultas duram 1 hora e começam na hora cheia; a última começa às 18:00 | 422 |
| O agendamento precisa de pelo menos 30 minutos de antecedência | 422 |
| Médico ou paciente inativo não pode ter consulta nova | 422 |
| O médico não pode ter duas consultas no mesmo horário | 409 |
| O paciente pode ter no máximo uma consulta por dia | 409 |
| O cancelamento exige um motivo e pelo menos 24 horas de antecedência | 400 sem motivo, 422 fora do prazo |

Se o pedido vier sem `medicoId`, a especialidade passa a ser obrigatória e a API escolhe um médico ativo dessa especialidade que esteja livre no horário (o de menor id). Se ninguém estiver livre, a resposta é 409.

Os horários são interpretados no fuso `America/Sao_Paulo`, configurável em `app.fuso-horario`.

Além da validação no serviço, existe um índice único parcial no banco (`medico_id, data_hora` para consultas não canceladas). Ele cobre o caso de duas requisições simultâneas pedirem o mesmo horário: a segunda recebe 409. A regra de uma consulta por dia do paciente não tem essa proteção no banco, só no serviço.

Excluir um médico ou paciente não apaga o registro: ele é marcado como inativo, some das listagens e deixa de aceitar consultas novas.

## Erros

Todas as respostas de erro seguem o formato ProblemDetail (RFC 9457). Erros de validação trazem a lista de campos:

```json
{
  "title": "Dados inválidos",
  "status": 400,
  "detail": "Um ou mais campos não passaram na validação",
  "instance": "/pacientes",
  "campos": [{ "campo": "cpf", "mensagem": "CPF inválido" }]
}
```

O CPF é conferido pelos dígitos verificadores e aceita entrada com ou sem máscara; no banco ele fica só com os números.

## Autenticação

Tudo exige um token JWT, menos o login, o Swagger e o `/actuator/health`. O token é assinado com HS256 usando a chave da variável `JWT_SECRET` e vale 2 horas.

Nenhuma senha ou chave está no repositório. A aplicação não sobe sem `JWT_SECRET` (com pelo menos 32 caracteres), e no perfil `prod` também não sobe sem `DB_URL`, `DB_USER` e `DB_PASSWORD`.

Não há cadastro de usuários pela API. No perfil `dev`, se `DEV_USER_LOGIN` e `DEV_USER_PASSWORD` estiverem definidas, um usuário com esses dados é criado na subida.

## Como rodar

### Com Docker Compose

```bash
cp .env.example .env
# edite o .env: troque as senhas e preencha JWT_SECRET (openssl rand -base64 48)
docker compose up --build
```

A API fica em `http://localhost:8080` e a documentação em `http://localhost:8080/swagger-ui.html`. Para parar e apagar o banco: `docker compose down -v`.

### Direto pela máquina

Precisa de JDK 25 ou mais novo e de um PostgreSQL acessível.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/clinica DB_USER=clinica DB_PASSWORD=...
export JWT_SECRET=$(openssl rand -base64 48)
export DEV_USER_LOGIN=recepcao DEV_USER_PASSWORD=...
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testes

```bash
./mvnw verify
```

Roda os testes de unidade das regras (Surefire) e depois os testes de integração (Failsafe, arquivos `*IT`), que sobem um PostgreSQL 18 com Testcontainers. Por isso os testes de integração precisam do Docker rodando.

## Exemplos com curl

```bash
# login
TOKEN=$(curl -s localhost:8080/auth/login -H 'Content-Type: application/json' \
  -d '{"login":"recepcao","senha":"sua-senha"}' | jq -r .token)

# cadastrar médico
curl -s localhost:8080/medicos -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{
  "nome": "Dra. Ana Souza", "email": "ana@clinica.dev", "telefone": "81999990000",
  "crm": "123456", "especialidade": "CARDIOLOGIA",
  "endereco": {"logradouro": "Rua A", "numero": "1", "bairro": "Centro",
               "cidade": "Recife", "uf": "PE", "cep": "50000-000"}}'

# cadastrar paciente
curl -s localhost:8080/pacientes -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{
  "nome": "João Lima", "email": "joao@exemplo.dev", "telefone": "81988880000",
  "cpf": "529.982.247-25",
  "endereco": {"logradouro": "Rua A", "numero": "1", "bairro": "Centro",
               "cidade": "Recife", "uf": "PE", "cep": "50000-000"}}'

# agendar com um médico específico
curl -s localhost:8080/consultas -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"medicoId": 1, "pacienteId": 1, "dataHora": "2026-11-23T10:00"}'

# agendar só pela especialidade (a API escolhe o médico)
curl -s localhost:8080/consultas -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"pacienteId": 2, "dataHora": "2026-11-23T10:00", "especialidade": "CARDIOLOGIA"}'

# listar consultas ativas de um médico, paginado
curl -s "localhost:8080/consultas?medicoId=1&page=0&size=10" -H "Authorization: Bearer $TOKEN"

# cancelar
curl -s localhost:8080/consultas/1/cancelamento -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"motivo": "PACIENTE_DESISTIU"}'
```

As datas dos exemplos precisam estar no futuro e cair num horário válido da clínica.

## Endpoints

| Método | Caminho | O que faz |
|---|---|---|
| POST | `/auth/login` | Devolve um token JWT |
| POST, GET | `/medicos` | Cadastra / lista médicos ativos (paginado, ordenado por nome) |
| GET, PUT, DELETE | `/medicos/{id}` | Detalha / atualiza nome, telefone e endereço / inativa |
| POST, GET | `/pacientes` | Cadastra / lista pacientes ativos |
| GET, PUT, DELETE | `/pacientes/{id}` | Detalha / atualiza / inativa |
| POST, GET | `/consultas` | Agenda / lista consultas não canceladas (filtro opcional `medicoId`) |
| GET | `/consultas/{id}` | Detalha uma consulta |
| POST | `/consultas/{id}/cancelamento` | Cancela com motivo |

Especialidades: `CARDIOLOGIA`, `DERMATOLOGIA`, `GINECOLOGIA`, `ORTOPEDIA`, `PEDIATRIA`.
Motivos de cancelamento: `PACIENTE_DESISTIU`, `MEDICO_CANCELOU`, `OUTROS`.

## O que ainda não faz

- Não tem papéis de usuário: qualquer usuário autenticado pode fazer tudo.
- Não há cadastro nem troca de senha pela API.
- Consultas canceladas ficam no banco, mas não aparecem na listagem (só pelo id).
