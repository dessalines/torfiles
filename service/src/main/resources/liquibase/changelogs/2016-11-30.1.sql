--liquibase formatted sql

--changeset tyler:1

create table torrent (
    id bigserial primary key,
    info_hash varchar(40) not null unique,
    name varchar(512),
    size_bytes bigint,
    age timestamp,
    seeders integer,
    leechers integer,
    magnet_link varchar(2000),
    bencode bytea,
    created timestamp default current_timestamp
);

--rollback drop table torrent cascade;

insert into torrent (info_hash, name, size_bytes, age, seeders, leechers)
    values ('c6ca71741152a467c0dbaaa9802bedd69dee1714', 'The Comintern (Dvdrip) XviD', 1400, '2016-10-01', 25, 32),
    ('c6ca71741152a467c0dbaaa9sdfbedd69dee1714', 'Blatherville(2001)', 1500, '2016-10-01', 18, 3);

create index idx_torrent_name on torrent(name);
create index idx_torrent_size_bytes on torrent(size_bytes);
create index idx_torrent_age on torrent(age);
create index idx_torrent_seeders on torrent(seeders);
create index idx_torrent_leechers on torrent(leechers);

