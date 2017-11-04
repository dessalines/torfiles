--liquibase formatted sql
--changeset tyler:1 splitStatements:false

create extension if not exists pg_trgm;

create table torrent (
    info_hash varchar(40) primary key,
    name varchar(2048) not null,
    size_bytes bigint not null,
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
    unique (info_hash, peer_address),
    constraint fk1_torrent foreign key (info_hash)
        references torrent (info_hash)
        on update cascade on delete cascade
);

--rollback drop table torrent_peer cascade;

create table file (
    id bigserial primary key,
    info_hash varchar(40) not null,
    path varchar(2048) not null,
    size_bytes bigint not null,
    index_ integer not null,
    text_search tsvector default null,
    created timestamp default current_timestamp,
    constraint fk1_torrent foreign key (info_hash)
        references torrent (info_hash)
        on update cascade on delete cascade
);

--rollback drop table file;

create materialized view file_view as
select
    f.id,
    f.info_hash,
    f.path,
    count(tp.peer_address) as peers,
    f.size_bytes,
    f.index_,
    f.text_search,
    f.created
from file as f
inner join torrent_peer as tp on f.info_hash = tp.info_hash
group by f.id, f.info_hash, f.path, f.size_bytes, f.index_, f.text_search, f.created;

--rollback drop view if exists file_view;


create index idx_file_info_hash on file_view(info_hash, path);
create index idx_file_text_search on file_view using gin (text_search);

CREATE FUNCTION file_vector_update() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        new.search_vector = to_tsvector(NEW.path);
    END IF;
    IF TG_OP = 'UPDATE' THEN
        IF NEW.name <> OLD.name THEN
            new.search_vector = to_tsvector(NEW.path);
        END IF;
    END IF;
    RETURN NEW;
END
$$ LANGUAGE 'plpgsql';

--rollback drop function file_vector_update();


CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE ON file
FOR EACH ROW EXECUTE PROCEDURE file_vector_update();

--rollback drop trigger if exists tsvectorupdate on file;

