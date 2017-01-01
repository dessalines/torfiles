package com.torshare.torrent;

import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.*;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import org.apache.commons.io.FileUtils;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
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

    private static Long REMOVE_AFTER_TIME = TimeUnit.MINUTES.toMillis(10);

    LibtorrentEngine() {

        System.setProperty("jlibtorrent.jni.path", DataSources.LIBTORRENT_PATH);
        log.info("Starting up libtorrent with version: " + LibTorrent.version());

        s = new SessionManager();

        dhtBootstrap();

        s.addListener(alerts());

//        s.maxActiveDownloads(-1);
//        s.maxActiveSeeds(-1);
//        s.downloadRateLimit(10000);

    }

    public void addTorrent(TorrentInfo ti) {

        log.info("Added torrent: " + ti.name());

        try {
            Path tempDir = Files.createTempDirectory("tmp");
            Tools.recursiveDeleteOnShutdownHook(tempDir);
            this.s.download(ti, tempDir.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        log.info("temp dir: " + tempDir.toAbsolutePath().toString());

    }

    public byte[] fetchMagnetURI(String uri) {

        log.info("Fetching the magnet uri, please wait...");
        byte[] data = s.fetchMagnet(uri, 90);

//        if (data == null) {
//            log.info("Failed to retrieve the magnet:" + uri.toString());
//        } else {
//            TorrentInfo ti = TorrentInfo.bdecode(data);
//            Tools.dbInit();
//            Actions.saveTorrentInfo(ti);
//            Tools.dbClose();
//            addTorrent(ti);
//        }

        return data;

    }

    public void scanForPeers() throws IOException {

        Tools.dbInit();
        Integer fetchLimit = 1000;
        Integer addThreshold = 500;


        Paginator p = new Paginator(Tables.Torrent.class,
                fetchLimit,
                "bencode is not null")
                .orderBy("peers asc nulls first");

        int c = 1;
        while (c < p.pageCount()) {

            // Add the next batch if torrents are below
            Long activeTorrents = s.swig().get_torrents().size();

            log.info("Current torrents: " + activeTorrents);

            if (activeTorrents < addThreshold) {
                log.info("Adding next batch of torrents.");
                List<Tables.Torrent> torrents = p.getPage(c++);
                for (Tables.Torrent t : torrents) {
                    byte[] data = t.getBytes("bencode");
                    addTorrent(TorrentInfo.bdecode(data));
                }

            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        Tools.dbClose();

        // Run it again
        scanForPeers();
    }

    private AlertListener alerts() {

        return new AlertListener() {
            @Override
            public int[] types() {
//                return null;
                return new int[]{
                        AlertType.TORRENT_ADDED.swig(),
                        AlertType.TRACKER_REPLY.swig(),
                        AlertType.DHT_REPLY.swig(),
                        AlertType.METADATA_RECEIVED.swig(),
                        AlertType.STATS.swig()
                };

            }


            @Override
            public void alert(Alert<?> alert) {
                AlertType type = alert.type();

//                log.info(alert.what());
//                log.info(alert.message());

                switch (type) {
                    case TORRENT_ADDED:
                        TorrentAddedAlert a = (TorrentAddedAlert) alert;
                        log.info("Resuming torrent: " + a.torrentName());
                        a.handle().resume();
//                        a.handle().setAutoManaged(false);
                        break;

                    case TRACKER_REPLY:
                        TrackerReplyAlert tra = (TrackerReplyAlert) alert;
                        log.info("list peers count: " + tra.handle().status().listPeers());
                        break;

                    case DHT_REPLY:
                        DhtReplyAlert dhtReply = (DhtReplyAlert) alert;
                        log.debug("dht reply peers: " + dhtReply.numPeers());
                        log.debug("num peers: " + dhtReply.handle().status().numPeers());
                        log.debug("num seeds: " + dhtReply.handle().status().numSeeds());
                        log.debug("list peers count: " + dhtReply.handle().status().listPeers());
                        log.debug("list seeds count: " + dhtReply.handle().status().listSeeds());
                        log.debug("num complete: " + dhtReply.handle().status().numComplete());
                        log.debug("num incomplete: " + dhtReply.handle().status().numIncomplete());
                        int peers = dhtReply.handle().status().listPeers();
                        int seeds = dhtReply.handle().status().listSeeds();

                        if (!dhtReply.handle().name().startsWith("fetch_magnet___magnet") && peers != 0) {
                            Tools.dbInit();
                            Actions.saveSeeders(dhtReply.handle().infoHash().toString(), seeds, peers);
                            Tools.dbClose();
//                            dhtReply.handle().pause();
                            s.remove(dhtReply.handle());
                        }

                        break;
                    case METADATA_RECEIVED:
                        MetadataReceivedAlert mar = (MetadataReceivedAlert) alert;
                        log.info("metadata received for " + mar.handle().name());

                        TorrentInfo ti = TorrentInfo.bdecode(mar.torrentData());
                        Tools.dbInit();
                        Actions.saveTorrentInfo(ti);
                        Tools.dbClose();
                        addTorrent(ti);

                        break;
                    case STATS:
                        StatsAlert sa = (StatsAlert) alert;
                        if (sa.handle().status().activeDuration() > REMOVE_AFTER_TIME) {
                            log.debug("torrent has been active for: " + sa.handle().status().activeDuration() + ", removing");
                            s.remove(sa.handle());
                        }
                        break;

                }
            }
        };
    }

    private void dhtBootstrap() {

        log.info("Bootstrapping DHT nodes...");
        final CountDownLatch signal = new CountDownLatch(1);

        // the session stats are posted about once per second.
        AlertListener l = new AlertListener() {
            @Override
            public int[] types() {
                return new int[]{AlertType.SESSION_STATS.swig(), AlertType.DHT_STATS.swig()};
            }

            @Override
            public void alert(Alert<?> alert) {

                if (alert.type().equals(AlertType.SESSION_STATS)) {
                    s.postDhtStats();
                }

                if (alert.type().equals(AlertType.DHT_STATS)) {

                    long nodes = s.stats().dhtNodes();
                    // wait for at least 10 nodes in the DHT.
                    if (nodes >= 10) {
                        signal.countDown();
                    }
                }
            }
        };

        s.addListener(l);
        s.start();
        s.postDhtStats();

        // waiting for nodes in DHT (10 seconds)
        boolean r = false;
        try {
            r = signal.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }


        // no more trigger of DHT stats
        s.removeListener(l);

        log.info("Done bootstrapping");
    }

}
