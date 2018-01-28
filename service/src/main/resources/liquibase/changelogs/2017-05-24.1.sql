--liquibase formatted sql
--changeset tyler:1 splitStatements:false

create extension if not exists pg_trgm;

create table torrent (
    id bigserial primary key,
    info_hash varchar(52) not null unique,
    name varchar(2048) not null,
    size_bytes bigint not null,
    bencode bytea not null,
    seeders bigint not null,
    leechers bigint not null,
    created timestamp default current_timestamp
);

create index idx_torrent on torrent(info_hash);

--rollback drop table torrent cascade;


create table file (
    id bigserial primary key,
    info_hash varchar(52) not null,
    path varchar(2048) not null,
    size_bytes bigint not null,
    index_ integer not null,
    text_search tsvector default null,
    created timestamp default current_timestamp,
    constraint fk1_torrent foreign key (info_hash)
        references torrent (info_hash)
        on update cascade on delete cascade
);

create index idx_file_infohash on file(info_hash);
create unique index idx_file_infohash_index on file(info_hash, index_);

--rollback drop table file;

create view table_count_view as
select reltuples::bigint AS count_ FROM pg_class where relname in ('file', 'torrent');

--rollback drop view if exists table_count_view;

create materialized view file_view as
select
    f.id,
    f.info_hash,
    f.path,
    t.seeders,
    t.leechers,
    f.size_bytes,
    f.index_,
    f.text_search,
    f.created
from file as f
left join torrent as t on f.info_hash = t.info_hash
group by f.id, f.info_hash, f.path, f.size_bytes, f.index_, f.text_search, f.created, t.seeders, t.leechers;

--rollback drop view if exists file_view;

create unique index idx_file_id on file_view(id);
create unique index idx_file_info_hash on file_view(info_hash, path);
create index idx_peer_size on file_view(seeders desc, size_bytes desc);
create index idx_file_text_search on file_view using gin (text_search);

drop function if exists file_vector_update;

CREATE FUNCTION file_vector_update() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        new.text_search = to_tsvector(regexp_replace(NEW.path, '[_\.\/-]',' ', 'g'));
    END IF;
    IF TG_OP = 'UPDATE' THEN
        IF NEW.name <> OLD.name THEN
            new.text_search = to_tsvector(regexp_replace(NEW.path, '[_\.\/-]',' ', 'g'));
        END IF;
    END IF;
    RETURN NEW;
END
$$ LANGUAGE 'plpgsql';

--rollback drop function file_vector_update;


CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE ON file
FOR EACH ROW EXECUTE PROCEDURE file_vector_update();

--rollback drop trigger if exists tsvectorupdate on file;

