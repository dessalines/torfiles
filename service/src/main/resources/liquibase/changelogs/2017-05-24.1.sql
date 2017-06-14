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

create materialized view file_view as
select
    file.id,
    file.path,file.size_bytes,
    file.index_,
    file.created,
    t.info_hash,
    t.peers
from file
inner join torrent as t on t.id = file.torrent_id
order by peers desc nulls last, size_bytes desc;

create index idx_file_view_path_tri on file_view using gin (path gin_trgm_ops);

--rollback drop materialized view file_view;
