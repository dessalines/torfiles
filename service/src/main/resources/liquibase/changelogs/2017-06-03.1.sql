--liquibase formatted sql
--changeset tyler:2

drop index idx_file_peers;
drop index idx_file_peers_desc;
create index idx_torrent_peers on torrent(peers);
create index idx_torrent_peers_desc on torrent(peers desc nulls last);

drop view file_view;

alter table file drop column peers;

create view file_view as
select file.*,
t.info_hash
from file
inner join torrent as t on t.id = file.torrent_id;
