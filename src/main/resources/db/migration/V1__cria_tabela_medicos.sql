create table medicos (
    id            bigint generated always as identity primary key,
    nome          varchar(100) not null,
    email         varchar(100) not null,
    telefone      varchar(20)  not null,
    crm           varchar(10)  not null unique,
    especialidade varchar(30)  not null,
    logradouro    varchar(100) not null,
    numero        varchar(20),
    complemento   varchar(100),
    bairro        varchar(100) not null,
    cidade        varchar(100) not null,
    uf            varchar(2)   not null,
    cep           varchar(8)   not null,
    ativo         boolean      not null default true
);

create index ix_medicos_especialidade_ativo on medicos (especialidade, ativo);
