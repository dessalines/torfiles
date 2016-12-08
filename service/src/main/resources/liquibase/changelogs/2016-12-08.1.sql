--liquibase formatted sql
--changeset tyler:2

ALTER TABLE torrent RENAME leechers TO peers;

--rollback alter table torrent rename peers to leechers;