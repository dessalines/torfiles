--liquibase formatted sql
--changeset tyler:1

create extension if not exists pg_trgm;

create table torrent (
    id bigserial primary key,
    info_hash varchar(40) not null unique,
    name varchar(2048) not null,
    size_bytes bigint not null,
    age timestamp not null,
    bencode bytea not null,
    created timestamp default current_timestamp
);

create index idx_torrent on torrent(info_hash);

--rollback drop table torrent cascade;

create table torrent_peer (
    id bigserial primary key,
    info_hash varchar(40) not null,
    peer_address varchar(16),
    created timestamp default current_timestamp,
    unique (info_hash, peer_address)
);

--rollback drop table torrent_peer

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
    f.id,
    t.info_hash,
    f.path,
    count(tp.peer_address) as peers,
    f.size_bytes,
    f.index_,
    f.created
from file as f
inner join torrent as t on t.id = f.torrent_id
inner join torrent_peer as tp on t.info_hash = tp.info_hash
group by f.id, t.info_hash, f.path, f.size_bytes, f.index_, f.created
order by peers desc nulls last, size_bytes desc;

--rollback drop view if exists file_view;

create table file_fast as select * from file_view;

--rollback drop table if exists file_fast;
