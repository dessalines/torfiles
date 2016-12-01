--liquibase formatted sql

--changeset tyler:1

create table torrent (
    id bigserial primary key,
    info_hash varchar(40) not null unique,
    name varchar(255),
    size_bytes bigint,
    age timestamp,
    seeders integer,
    leechers integer,
    created timestamp default current_timestamp
);

--rollback drop table torrent cascade;

create table torrent_tracker (
    id bigserial primary key,
    torrent_id bigint not null,
    tracker varchar(255),
    tier smallint not null default 0,
    created timestamp default current_timestamp,
    constraint fk1_ foreign key (torrent_id)
            references torrent (id)
            on update cascade on delete cascade
);

-- rollback drop table torrent_tracker cascade;

insert into torrent (info_hash, name, size_bytes, age, seeders, leechers)
    values ('c6ca71741152a467c0dbaaa9802bedd69dee1714', 'The Corporation 2003 (Dvdrip) XviD', 1400, '2016-10-01', 25, 32);
insert into torrent (info_hash, name, size_bytes, age, seeders, leechers)
    values ('c6ca71741152a467c0dbaaa9sdfbedd69dee1714', 'Blatherville(2001)', 1500, '2016-10-01', 1, 3);

insert into torrent_tracker (torrent_id, tracker)
    values (1, 'udp://tracker.coppersurfer.tk:6969');
insert into torrent_tracker (torrent_id, tracker)
    values (2, 'udp://tracker.coppersurfer.tk:6969');


create index idx_torrent_name on torrent(name);
create index idx_torrent_size_bytes on torrent(size_bytes);
create index idx_torrent_age on torrent(age);
create index idx_torrent_seeders on torrent(seeders);
create index idx_torrent_leechers on torrent(leechers);

