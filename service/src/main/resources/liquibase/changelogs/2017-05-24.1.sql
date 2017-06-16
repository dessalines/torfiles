--liquibase formatted sql
--changeset tyler:1

create extension if not exists pg_trgm;

create table torrent (
    id bigserial primary key,
    info_hash varchar(40) not null unique,
    name varchar(2048) not null,
    size_bytes bigint not null,
    age timestamp not null,
    peers integer,
    created timestamp default current_timestamp
);

create index idx_torrent on torrent(info_hash);

--rollback drop table torrent cascade;

create table file (
    id bigserial primary key,
    torrent_id bigint not null,
    path varchar(2048) not null,
    size_bytes bigint not null,
    index_ integer not null,
    created timestamp default current_timestamp,
    constraint fk1_torrent foreign key (torrent_id)
        references torrent (id)
        on update cascade on delete cascade
);

create index idx_file_torrent_id on file(torrent_id);

--rollback drop table file;

create view file_view as
select
    file.id,
    t.info_hash,
    file.path,
    t.peers,
    file.size_bytes,
    file.index_,
    file.created
from file
inner join torrent as t on t.id = file.torrent_id
order by peers desc nulls last, size_bytes desc;

--rollback drop view if exists file_view;
