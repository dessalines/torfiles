package com.torshare.torrent;

import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.*;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import org.apache.commons.io.FileUtils;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tyler on 12/2/16.
 */
public enum LibtorrentEngine {

    INSTANCE;

    private Logger log = LoggerFactory.getLogger(LibtorrentEngine.class);

    private SessionManager s;

    LibtorrentEngine() {

        System.setProperty("jlibtorrent.jni.path", DataSources.LIBTORRENT_PATH);
        log.info("Starting up libtorrent with version: " + LibTorrent.version());

        s = new SessionManager();

        s.start();
//        s.pause();

        s.maxActiveDownloads(-1);
        s.maxActiveSeeds(-1);
        s.downloadRateLimit(1000);




        try {
            setupAlerts();
//            dhtBootstrap();
        } catch(Throwable e) {
            e.printStackTrace();
        }

    }

    public void addTorrent(TorrentInfo ti) throws IOException {

        log.info("Added torrent: " + ti.name());

        Path tempDir = Files.createTempDirectory("tmp");
        Tools.recursiveDeleteOnShutdownHook(tempDir);

        this.s.download(ti, tempDir.toFile());

//        log.info("temp dir: " + tempDir.toAbsolutePath().toString());

//
//
//        ArrayList<TcpEndpoint> asdf = s.dhtGetPeers(ti.infoHash(),120);
//
//        log.info("dzht peers = " + asdf.size());
    }

    public byte[] fetchMagnetURI(String uri) {
        log.info("Fetching the magnet uri, please wait...");
        byte[] data = s.fetchMagnet(uri, 120);

        if (data != null) {
//            log.info(Entry.bdecode(data).toString());
        } else {
            throw new NoSuchElementException("Failed to retrieve the magnet:" + uri.toString());
        }

        return data;

    }

    public void addTorrentsOnStartup() throws IOException {

        Tools.dbInit();

        LazyList<Tables.Torrent> torrents = Tables.Torrent.find("bencode is not null");

        for (Tables.Torrent t : torrents) {
            byte[] data = t.getBytes("bencode");
            addTorrent(TorrentInfo.bdecode(data));
        }

        Tools.dbClose();
    }

    private void setupAlerts() {

        s.addListener(new AlertListener() {
            @Override
            public int[] types() {
                return null;
            }



            @Override
            public void alert(Alert<?> alert) {
                AlertType type = alert.type();

                log.info(alert.what());
                log.info(alert.message());

                switch (type) {
                    case TORRENT_ADDED:
                        TorrentAddedAlert a = (TorrentAddedAlert) alert;
                        a.handle().resume();
                        a.handle().setAutoManaged(false);
                        break;

                    case TRACKER_REPLY:
                        TrackerReplyAlert tra = (TrackerReplyAlert) alert;
                        log.info("list peers count: " + tra.handle().status().listPeers());
                        break;

                    case DHT_REPLY:
                        DhtReplyAlert dhtReply = (DhtReplyAlert) alert;
                        log.info("dht reply peers: " + dhtReply.numPeers());
                        log.info("num peers: " + dhtReply.handle().status().numPeers());
                        log.info("num seeds: " + dhtReply.handle().status().numSeeds());
                        log.info("list peers count: " + dhtReply.handle().status().listPeers());
                        log.info("list seeds count: " + dhtReply.handle().status().listSeeds());
                        log.info("num complete: " + dhtReply.handle().status().numComplete());
                        log.info("num incomplete: " + dhtReply.handle().status().numIncomplete());
                        log.info("creation date: " + dhtReply.handle().torrentFile().creationDate());
                        int peers = dhtReply.handle().status().listPeers();
                        int seeds = dhtReply.handle().status().listSeeds();

                        if (peers != 0) {
                            Tools.dbInit();
                            Actions.saveSeeders(dhtReply.handle().infoHash().toString(), seeds, peers);
                            Tools.dbClose();
                        }

                        break;

                }



            }
        });
    }

}
