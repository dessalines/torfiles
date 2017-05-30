--liquibase formatted sql
--changeset tyler:1


create table torrent (
    id bigserial primary key,
    info_hash varchar(40) not null unique,
    name varchar(2048) not null,
    size_bytes bigint not null,
    age timestamp not null,
    peers integer,
    created timestamp default current_timestamp
);

--rollback drop table torrent cascade;

create table file (
    id bigserial primary key,
    torrent_id bigint not null,
    path varchar(2048) not null,
    size_bytes bigint not null,
    index_ integer not null,
    peers integer,
    created timestamp default current_timestamp,
    constraint fk1_torrent foreign key (torrent_id)
        references torrent (id)
        on update cascade on delete cascade
);

create extension if not exists pg_trgm;

create index idx_file_path on file using gist (path gist_trgm_ops);
create index idx_file_peers on file(peers desc nulls last);
create index idx_file_size on file(size_bytes desc nulls last);

--rollback drop table file;

create view file_view as
select file.*,
t.info_hash
from file
inner join torrent as t on t.id = file.torrent_id;

--rollback drop view file_view;

