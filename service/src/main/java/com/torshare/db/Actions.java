package com.torshare.db;

import ch.qos.logback.classic.Logger;
import com.frostwire.jlibtorrent.TorrentInfo;
import org.javalite.activejdbc.DB;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

import static com.torshare.db.Tables.File;
import static com.torshare.db.Tables.Torrent;

/**
 * Created by tyler on 11/30/16.
 */
public class Actions {

    public static Logger log = (Logger) LoggerFactory.getLogger(Actions.class);

    public static Torrent saveTorrentInfo(TorrentInfo ti) {



        Torrent torrent = Torrent.findFirst("info_hash = ?", ti.infoHash().toString());

        if (torrent != null) {
            return torrent;
        }

        new DB("default").openTransaction();

        Timestamp age = (ti.creationDate() != 0) ? new Timestamp(ti.creationDate()*1000L) : new Timestamp(System.currentTimeMillis());

        torrent = Torrent.createIt(
                "info_hash", ti.infoHash().toString(),
                "name", ti.name(),
                "size_bytes", ti.totalSize(),
                "age", age);

        // Save the file info
        for (int i = 0; i < ti.files().numFiles(); i++) {
            File.createIt(
                    "torrent_id", torrent.getLongId(),
                    "path", ti.files().filePath(i),
                    "size_bytes", ti.files().fileSize(i),
                    "index_", i);
        }

        new DB("default").commitTransaction();

        log.debug("Saving torrent: " + torrent.toJson(true));

        return torrent;

    }

    public static void savePeers(String infoHash, int peers) {
        Torrent torrent = Torrent.findFirst("info_hash = ?", infoHash);

        if (torrent != null) {
            torrent.set("peers", peers).saveIt();

            File.update("peers = ?", "torrent_id = ?", peers, torrent.getLongId());

            log.debug("Saving peers for torrent: " + torrent.getString("name"));
        }



    }

}
