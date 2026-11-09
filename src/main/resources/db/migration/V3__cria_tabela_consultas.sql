create table consultas (
    id                   bigint generated always as identity primary key,
    medico_id            bigint      not null references medicos (id),
    paciente_id          bigint      not null references pacientes (id),
    data_hora            timestamp   not null,
    motivo_cancelamento  varchar(30),
    cancelada_em         timestamp,
    criada_em            timestamp   not null default now()
);

-- Garante no banco o que o serviço já valida: um médico não pode ter
-- duas consultas ativas no mesmo horário, mesmo com requisições simultâneas.
create unique index ux_consultas_medico_horario_ativa
    on consultas (medico_id, data_hora)
    where motivo_cancelamento is null;

create index ix_consultas_paciente_data on consultas (paciente_id, data_hora);
