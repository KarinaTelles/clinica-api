create table pacientes (
    id          bigint generated always as identity primary key,
    nome        varchar(100) not null,
    email       varchar(100) not null,
    telefone    varchar(20)  not null,
    cpf         varchar(11)  not null unique,
    logradouro  varchar(100) not null,
    numero      varchar(20),
    complemento varchar(100),
    bairro      varchar(100) not null,
    cidade      varchar(100) not null,
    uf          varchar(2)   not null,
    cep         varchar(8)   not null,
    ativo       boolean      not null default true
);
