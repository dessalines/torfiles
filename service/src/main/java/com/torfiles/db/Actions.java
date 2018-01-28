package com.torfiles.db;

import ch.qos.logback.classic.Logger;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.torfiles.tools.Tools;
import org.javalite.activejdbc.DB;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

import static com.torfiles.db.Tables.File;
import static com.torfiles.db.Tables.Torrent;

/**
 * Created by tyler on 11/30/16.
 */
public class Actions {

    public static Logger log = (Logger) LoggerFactory.getLogger(Actions.class);


    public static Torrent saveTorrentInfo(TorrentInfo ti, Long seeders, Long leechers) {

        try {
            new DB("default").openTransaction();

            Torrent torrent = Torrent.findFirst("info_hash = ?", ti.infoHash().toString());

            if (torrent != null) {
                return torrent;
            }

            Timestamp age = (ti.creationDate() != 0) ? new Timestamp(ti.creationDate() * 1000L) : new Timestamp(System.currentTimeMillis());

            torrent = Torrent.createIt(
                    "info_hash", ti.infoHash().toString(),
                    "name", ti.name(),
                    "size_bytes", ti.totalSize(),
                    "bencode", ti.bencode(),
                    "seeders", seeders,
                    "leechers", leechers);

            // Save the file info
            for (int i = 0; i < ti.files().numFiles(); i++) {

                File.createIt(
                        "info_hash", ti.infoHash().toString(),
                        "path", ti.files().filePath(i),
                        "size_bytes", ti.files().fileSize(i),
                        "index_", i);
            }

            log.info("Saving torrent: " + torrent.getString("name"));

            return torrent;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            new DB("default").commitTransaction();
        }


    }

    public static void savePeers(String infoHash, int peers) {
        try {
            new DB("default").openTransaction();

//            log.debug("Trying to save peers: " + infoHash);

            Torrent torrent = Torrent.findFirst("info_hash = ? and (peers is null or peers != ?)", infoHash, peers);

            if (torrent != null) {

                torrent.set("peers", peers).saveIt();

                log.debug("Saving peers for torrent: " + torrent.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new DB("default").commitTransaction();
        }


    }

}
