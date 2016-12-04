package com.torshare.db;

import ch.qos.logback.classic.Logger;
import com.frostwire.jlibtorrent.AnnounceEntry;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.torshare.torrent.LibtorrentEngine;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.NoSuchElementException;

import static com.torshare.db.Tables.*;

/**
 * Created by tyler on 11/30/16.
 */
public class Actions {

    public static Logger log = (Logger) LoggerFactory.getLogger(Actions.class);

    public static Torrent saveTorrentInfo(TorrentInfo ti) {

        Torrent torrent = Torrent.findFirst("info_hash = ?", ti.infoHash().toString());

        if (torrent != null) {
            throw new NoSuchElementException("Torrent already exists.");
        }

        torrent = Torrent.createIt(
                "info_hash", ti.infoHash().toString(),
                "name", ti.name(),
                "size_bytes", ti.totalSize(),
                "age", new Timestamp(new Long(ti.creationDate())*1000L),
                "magnet_link", ti.makeMagnetUri(),
                "bencode", ti.bencode());

        log.info("saving torrent: " + torrent.toJson(true));

        return torrent;

    }

    public static void saveSeeders(String infoHash, int seeders, int leechers) {
        Torrent torrent = Torrent.findFirst("info_hash = ?", infoHash);

        torrent.set(
                "seeders", seeders,
                "leechers", leechers)
                .saveIt();
    }


}
