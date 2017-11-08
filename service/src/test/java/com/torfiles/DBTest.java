package com.torfiles;

import com.torfiles.tools.Tools;
import com.torfiles.torrent.LibtorrentEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by tyler on 12/4/16.
 */
public class DBTest {

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
    public void infoHashParse() throws Exception {

        String uri = "magnet:?xt=urn:btih:aebe4853b4b7679c61a8377bd63b8833e41b4c6d&dn=Captain.America.Civil.War.2016.720p.BluRay.X264-AMIABLE%5BEtHD%5D&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
        String infoHash = uri.split("btih:")[1].substring(0,40);

        assertEquals(infoHash, "aebe4853b4b7679c61a8377bd63b8833e41b4c6d");
    }

}
