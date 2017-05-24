--liquibase formatted sql
--changeset tyler:1

create table torrent (
    id bigserial primary key,
    info_hash varchar(40) not null unique,
    name varchar(2048) not null,
    size_bytes bigint not null,
    age timestamp not null,
    seeders integer,
    peers integer,
    created timestamp default current_timestamp
);

create index idx_torrent_name on torrent(name);
create index idx_torrent_size_bytes on torrent(size_bytes);
create index idx_torrent_age on torrent(age);
create index idx_torrent_seeders on torrent(seeders);
create index idx_torrent_leechers on torrent(peers);

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

create index idx_file_path_and_index on file(path, index_);
create index idx_file_path on file(path);

--rollback drop table file;

