--liquibase formatted sql
--changeset tyler:3

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
