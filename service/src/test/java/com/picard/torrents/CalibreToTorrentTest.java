package com.picard.torrents;

import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.InterruptedIOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class CalibreToTorrentTest {

    private Collection collection;

    @Before
    public void setUp() {
        DataSources.SOURCE_CODE_HOME = new File(System.getProperty("user.dir"));
        System.out.println(DataSources.LIBTORRENT_OS_LIBRARY_PATH());
        System.setProperty("jlibtorrent.jni.path", DataSources.LIBTORRENT_OS_LIBRARY_PATH());
        System.out.println("Libtorrent Version: " + LibTorrent.version());
    }

    @Test
    public void dhtListening() throws Throwable {
        System.out.println("Using libtorrent version: " + LibTorrent.version());

        File torrentFile = new File("/home/tyler/Sync/Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines.torrent");

        final SessionManager s = new SessionManager();

        final CountDownLatch signal = new CountDownLatch(1);

        s.addListener(new AlertListener() {
            @Override
            public int[] types() {
                return null;
            }

            @Override
            public void alert(Alert<?> alert) {
                AlertType type = alert.type();

                switch (type) {
                    case TORRENT_ADDED:
                        System.out.println("Torrent added");
                        ((TorrentAddedAlert) alert).handle().resume();
                        break;
                    case BLOCK_FINISHED:
                        BlockFinishedAlert a = (BlockFinishedAlert) alert;
                        int p = (int) (a.handle().status().progress() * 100);
                        System.out.println("Progress: " + p + " for torrent name: " + a.torrentName());
                        System.out.println(s.stats().totalDownload());
                        System.out.println("DHT?" + s.isDhtRunning() + " nodes: " + s.dhtNodes());

                        break;
                    case TORRENT_FINISHED:
                        System.out.println("Torrent finished");
//                        signal.countDown();
                        break;
                    case DHT_ANNOUNCE:
                        DhtAnnounceAlert b = (DhtAnnounceAlert) alert;
                        System.out.println(b.toString());
                        System.out.println("dht announce.");
                        break;
                    case DHT_PKT:
                        System.out.println(alert.toString());
                        System.out.println("dht pkt.");
                        break;
                    case DHT_LOG:
                        System.out.println(alert.toString());
                }
            }
        });

        s.start();
        s.startDht();
        System.out.println(s.isDhtRunning());
        TorrentInfo ti = new TorrentInfo(torrentFile);
        s.download(ti, torrentFile.getParentFile());




        signal.await();

    }

}

