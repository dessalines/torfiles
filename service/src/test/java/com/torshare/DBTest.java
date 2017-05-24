package com.torshare;

import com.frostwire.jlibtorrent.AlertListener;
import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import com.torshare.torrent.LibtorrentEngine;
import com.torshare.types.TorrentDetail;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tyler on 12/4/16.
 */
public class DBTest {

    File ubuntuTorrent = new File(DataSources.UBUNTU_TORRENT);
    File trotskyTorrent = new File(DataSources.TROTSKY_TORRENT);
    String ubuntuInfoHash = "0403fb4728bd788fbcb67e87d6feb241ef38c75a";
    String trotskyInfoHash = "d1f28f0c1b89ddd9a39205bef0be3715d117f91b";
    LibtorrentEngine lte;

    @Before
    public void setUp() throws Exception {
        lte = LibtorrentEngine.INSTANCE;
        Tools.dbInit();
    }

    @After
    public void tearDown() throws Exception {
        Tools.dbClose();
    }

    @Test
    public void readTorrent() throws Exception {
        TorrentInfo ti = new TorrentInfo(ubuntuTorrent);
        assertEquals(ubuntuInfoHash, ti.infoHash().toString());
    }


    @Test(expected=NoSuchElementException.class)
    public void testAlreadyExists() throws Exception {
        TorrentInfo ti = new TorrentInfo(ubuntuTorrent);
        Actions.saveTorrentInfo(ti);
        Actions.saveTorrentInfo(ti);
    }

    @Test
    public void testSaveTorrentInfo() throws Exception {
        TorrentInfo ti = new TorrentInfo(ubuntuTorrent);
        Tables.Torrent t = Tables.Torrent.findFirst("info_hash = ?", ti.infoHash().toString());
        if (t != null) t.delete();
        t = Actions.saveTorrentInfo(ti);
        assertEquals(ubuntuInfoHash, t.getString("info_hash"));

        ti = new TorrentInfo(trotskyTorrent);
        t = Tables.Torrent.findFirst("info_hash = ?", ti.infoHash().toString());
        if (t != null) t.delete();
        t = Actions.saveTorrentInfo(ti);
        assertEquals(trotskyInfoHash, t.getString("info_hash"));

    }

//    @Test
    public void fetchMagnetURI() throws Exception {


        String infoHash = "a83cc13bf4a07e85b938dcf06aa707955687ca7c";
        String uri = "magnet:?xt=urn:btih:" + infoHash;

        Tables.Torrent t = Tables.Torrent.findFirst("info_hash = ?", infoHash);
        if (t != null) t.delete();

        LibtorrentEngine lte = LibtorrentEngine.INSTANCE;

        byte[] data = lte.fetchMagnetURI(uri);

        assertNotNull(data);
    }

    @Test
    public void testTorrentDetail() throws Exception {
        TorrentInfo ti = new TorrentInfo(trotskyTorrent);
        TorrentDetail td = TorrentDetail.create(ti, 0, 0);

        assertEquals("Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines", td.getName());
        assertEquals("Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines/Trotsky - Fascism - What it is and How to Fight it - 00 - 1969 Introduction.mp3",
                td.getFiles().get(0));
        assertEquals(trotskyInfoHash, td.getInfoHash());
    }

    @Test
    public void infoHashParse() throws Exception {

        String uri = "magnet:?xt=urn:btih:aebe4853b4b7679c61a8377bd63b8833e41b4c6d&dn=Captain.America.Civil.War.2016.720p.BluRay.X264-AMIABLE%5BEtHD%5D&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
        String infoHash = uri.split("btih:")[1].substring(0,40);

        assertEquals(infoHash, "aebe4853b4b7679c61a8377bd63b8833e41b4c6d");
    }

    @Test
    public void csvTest() throws Exception {
        LazyList<Tables.Torrent> ts = Tables.Torrent.findAll();
        String csv = Tools.torrentsToCsv(ts);
        assertTrue(csv.contains(ubuntuInfoHash));
    }

    @Test
    public void fileTest() throws Exception {
        Tables.Torrent t = Tables.Torrent.findFirst("info_hash = ?", trotskyInfoHash);
        LazyList<Tables.File> files = Tables.File.find("torrent_id = ?", t.getLongId());
        Tables.File secondFile = files.get(1);
        assertTrue(secondFile.getString("path").equals("Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines/Trotsky - Fascism - What it is and How to Fight it - 01 - Fascism -- What Is It.mp3"));
        assertEquals(secondFile.getInteger("index_").intValue(), 1);
    }



}
