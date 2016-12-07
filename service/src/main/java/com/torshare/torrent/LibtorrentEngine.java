package com.torshare.torrent;

import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.*;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
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
        s.pause();

        s.maxActiveDownloads(9999);
        s.maxActiveSeeds(9999);


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
        tempDir.toFile().deleteOnExit();

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

        Map<String, Integer> trackerCount = new HashMap<>();

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
                    // TODO
//                     add metadata received alert, because torrentAdded fires for magnets, without trackers populated
                    case TORRENT_ADDED:
                        TorrentAddedAlert a = (TorrentAddedAlert) alert;
                        trackerCount.put(a.handle().infoHash().toString(), 0);
                        a.handle().scrapeTracker(); // TDOO need to make this periodic
                        a.handle().forceDHTAnnounce();

                        break;
                    case METADATA_RECEIVED:
                        MetadataReceivedAlert x = (MetadataReceivedAlert) alert;
                        trackerCount.put(x.handle().infoHash().toString(), 0);
                        x.handle().scrapeTracker(); // TDOO need to make this periodic
                        x.handle().forceDHTAnnounce();
                        break;

                    case SCRAPE_REPLY:
                        ScrapeReplyAlert c = (ScrapeReplyAlert) alert;
                        Tools.dbInit();
                        Integer countz = trackerCount.get(c.handle().infoHash().toString()) + 1;
                        if (c.getComplete() == 0 && countz < c.handle().trackers().size()) {
                            c.handle().swig().scrape_tracker(countz);
                            trackerCount.put(c.handle().infoHash().toString(), countz);
                        } else {
                            trackerCount.remove(c.handle().infoHash().toString());
                            Actions.saveSeeders(c.handle().infoHash().toString(), c.getComplete(), c.getIncomplete());
                        }
                        Tools.dbClose();
                        break;
                    case SCRAPE_FAILED:
                        ScrapeFailedAlert v = (ScrapeFailedAlert) alert;
                        Integer count = trackerCount.get(v.handle().infoHash().toString()) + 1;
                        if (count < v.handle().trackers().size()) {
                            trackerCount.put(v.handle().infoHash().toString(), count);
                            v.handle().swig().scrape_tracker(count);
                        }
                        break;
//                    case DHT_GET_PEERS:
//                        DhtGetPeersAlert e = (DhtGetPeersAlert) alert;
//                        log.info(e.message());
//                        break;
                    case DHT_GET_PEERS_REPLY:
                        DhtGetPeersReplyAlert d = (DhtGetPeersReplyAlert) alert;
//                        log.info(d.infoHash());
//                        log.info(d.numPeers());
                        break;

                }



            }
        });
    }

}
