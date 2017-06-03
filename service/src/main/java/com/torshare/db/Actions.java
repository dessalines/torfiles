package com.torshare.db;

import ch.qos.logback.classic.Logger;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.torshare.tools.Tools;
import org.javalite.activejdbc.DB;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Timestamp;

import static com.torshare.db.Tables.File;
import static com.torshare.db.Tables.Torrent;

/**
 * Created by tyler on 11/30/16.
 */
public class Actions {

    public static Logger log = (Logger) LoggerFactory.getLogger(Actions.class);


    public static Torrent saveTorrentInfo(java.io.File torrentFile) {

        String infoHash = torrentFile.getName().split(".torrent")[0];
        log.debug("Trying to save torrent: " + infoHash);
        Torrent torrent = Torrent.findFirst("info_hash = ?", infoHash);

        if (torrent != null) {
            return torrent;
        }

        byte[] bytes = Tools.readFileBytes(torrentFile);
        TorrentInfo ti = TorrentInfo.bdecode(bytes);

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

        log.debug("Saving torrent: " + torrent.toJson(true));

        return torrent;

    }

    public static void savePeers(String infoHash, int peers) {
        // TODO only update ones with null peers for now
        Torrent torrent = Torrent.findFirst("info_hash = ? and peers is null", infoHash);

        if (torrent != null) {
            torrent.set("peers", peers).saveIt();

            File.update("peers = ?", "torrent_id = ?", peers, torrent.getLongId());

            log.debug("Saving peers for torrent: " + torrent.getString("name"));
        }



    }

}
