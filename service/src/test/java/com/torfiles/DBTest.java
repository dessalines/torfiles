package com.torfiles;

import com.frostwire.jlibtorrent.TorrentInfo;
import com.torfiles.db.Actions;
import com.torfiles.db.Tables;
import com.torfiles.tools.DataSources;
import com.torfiles.tools.Tools;
import com.torfiles.torrent.LibtorrentEngine;
import com.torfiles.types.TorrentDetail;
import org.javalite.activejdbc.LazyList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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

    @Before
    public void setUp() throws Exception {
        LibtorrentEngine lte = LibtorrentEngine.INSTANCE;
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

    @Test
    public void testSaveTorrentInfo() throws Exception {
        Tables.Torrent t = Tables.Torrent.findFirst("info_hash = ?", ubuntuInfoHash);
        if (t != null) t.delete();
        t = Actions.saveTorrentInfo(ubuntuTorrent);
        assertEquals(ubuntuInfoHash, t.getString("info_hash"));

        Tables.Torrent t2 = Tables.Torrent.findFirst("info_hash = ?", trotskyInfoHash);
        if (t2 != null) t2.delete();
        t2 = Actions.saveTorrentInfo(trotskyTorrent);
        assertEquals(trotskyInfoHash, t2.getString("info_hash"));

    }


    @Test
    public void testTorrentDetail() throws Exception {

        Tables.Torrent t2 = Tables.Torrent.findFirst("info_hash = ?", trotskyInfoHash);
        if (t2 != null) t2.delete();
        Tables.Torrent t = Actions.saveTorrentInfo(trotskyTorrent);
        assertEquals(trotskyInfoHash, t.getString("info_hash"));

        Tables.Torrent torrent = Tables.Torrent.findFirst("info_hash = ?", trotskyInfoHash);
        LazyList<Tables.File> files = Tables.File.where("torrent_id = ?", torrent.getLongId()).orderBy("index_");

        TorrentDetail td = TorrentDetail.create(torrent, files);

        assertEquals("Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines", td.getName());
        assertEquals("Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines/Trotsky - Fascism - What it is and How to Fight it - 00 - 1969 Introduction.mp3",
                td.getFiles().get(0).getPath());
        assertEquals(trotskyInfoHash, td.getInfoHash());
    }

    @Test
    public void infoHashParse() throws Exception {

        String uri = "magnet:?xt=urn:btih:aebe4853b4b7679c61a8377bd63b8833e41b4c6d&dn=Captain.America.Civil.War.2016.720p.BluRay.X264-AMIABLE%5BEtHD%5D&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
        String infoHash = uri.split("btih:")[1].substring(0,40);

        assertEquals(infoHash, "aebe4853b4b7679c61a8377bd63b8833e41b4c6d");
    }

    @Test
    public void fileTest() throws Exception {
        Tables.Torrent t = Tables.Torrent.findFirst("info_hash = ?", trotskyInfoHash);
        LazyList<Tables.File> files = Tables.File.find("torrent_id = ?", t.getLongId()).orderBy("index_");
        Tables.File secondFile = files.get(0);
        assertEquals(secondFile.getString("path"), "Trotsky - Fascism - What it is and How to Fight it [audiobook] by dessalines/Trotsky - Fascism - What it is and How to Fight it - 00 - 1969 Introduction.mp3");
        assertEquals(secondFile.getInteger("index_").intValue(), 0);
    }



}
