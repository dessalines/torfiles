package com.torfiles.torrent;

import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.torfiles.db.Tables;
import com.torfiles.tools.DataSources;
import com.torfiles.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Created by tyler on 12/2/16.
 */
public enum LibtorrentEngine {

    INSTANCE;

    private Logger log = LoggerFactory.getLogger(LibtorrentEngine.class);
    final SessionManager s;

    LibtorrentEngine() {

        System.setProperty("jlibtorrent.jni.path", DataSources.LIBTORRENT_PATH);
        log.info("Starting up libtorrent with version: " + LibTorrent.version());

        s = new SessionManager();
        addAlertListeners();
    }

    private void addAlertListeners() {

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

    }

    public TorrentInfo fetchMagnet(String uri) {
        String infoHash = uri.substring(20,60);

        try {
            Tools.dbInit();
            Tables.Torrent torrent = Tables.Torrent.findFirst("info_hash = ?", infoHash);
            if (torrent != null) {
                return TorrentInfo.bdecode(torrent.getBytes("bencode"));
            }
        } finally {
            Tools.dbClose();
        }



        log.info("Downloading magnet: " + uri);

        // Fetching the magnet uri, waiting 30 seconds max

        byte[] data = s.fetchMagnet(uri, 30);

        TorrentInfo ti = TorrentInfo.bdecode(data);

        return ti;

    }
}


