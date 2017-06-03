--liquibase formatted sql
--changeset tyler:2

drop view file_view;

create view file_view as
select file.*,
t.info_hash,
t.peers
from file
inner join torrent as t on t.id = file.torrent_id;

--rollback drop view file_view;