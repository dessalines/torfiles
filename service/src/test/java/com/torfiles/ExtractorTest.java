package com.torfiles;

import com.torfiles.crawler.SiteMagnet;
import com.torfiles.crawler.sites.MagnetExtractor;
import com.torfiles.crawler.sites.SkyTorrentsExtractor;
import com.torfiles.crawler.sites.ThePirateBayExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class ExtractorTest {

    @Test
    public void skyTorrentsTest() throws Exception {
        MagnetExtractor me = new SkyTorrentsExtractor();

        Document doc = Jsoup.connect("https://www.skytorrents.in/info/9d9b0a063b9dd4aad72dfb6e62617e343ab024f8/The-Shape-of-Water-2017-DVDScr-XVID-AC3-HQ-Hive-CM8-EtMovies/?l=en-us").get();

        SiteMagnet s = me.extractMagnetLink(doc.html());

        assertTrue(s.getSeeders().equals(6599L));
        assertTrue(s.getLeechers().equals(2842L));
        assertTrue(s.getUri().equals("magnet:?xt=urn:btih:9d9b0a063b9dd4aad72dfb6e62617e343ab024f8&dn=The.Shape.of.Water.2017.DVDScr.XVID.AC3.HQ.Hive-CM8%5bEtMovies%5d&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969&tr=udp%3A%2F%2Feddie4.nl%3A6969&tr=udp%3A%2F%2Ftracker.pirateparty.gr%3A6969&tr=udp%3A%2F%2Fopentrackr.org%3A1337&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337"));

        Document doc2 = Jsoup.connect("https://www.skytorrents.in/info/c602f490e13474fa580bbbc56e868977a6ffdfa5/Grand-Theft-Auto-IV-Razor1911/?l=ja-jp:").get();

        SiteMagnet s2 = me.extractMagnetLink(doc2.html());

        assertTrue(s2.getSeeders().equals(4181L));
        assertTrue(s2.getLeechers().equals(450L));
        assertTrue(s2.getUri().equals("magnet:?xt=urn:btih:c602f490e13474fa580bbbc56e868977a6ffdfa5&dn=Grand%20Theft%20Auto%20IV-Razor1911&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969&tr=udp%3A%2F%2Feddie4.nl%3A6969&tr=udp%3A%2F%2Ftracker.pirateparty.gr%3A6969&tr=udp%3A%2F%2Fopentrackr.org%3A1337&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337"));


    }

    @Test
    public void thePirateBayTest() throws Exception {
        MagnetExtractor me = new ThePirateBayExtractor();

        Document doc = Jsoup.connect("https://thepiratebay.org/torrent/18346837/Star_Ocean_-_The_Second_Story_(USA)").get();

        SiteMagnet s = me.extractMagnetLink(doc.html());

        assertTrue(s.getSeeders().equals(1L));
        assertTrue(s.getLeechers().equals(0L));
        assertTrue(s.getUri().equals("magnet:?xt=urn:btih:f2467748444d5f312921878a2f8b5081221c2054&dn=Star+Ocean+-+The+Second+Story+%28USA%29&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"));

        Document doc2 = Jsoup.connect("https://thepiratebay.org/torrent/19107645/Geostorm.2017.720p.HC.HDRip.850MB").get();

        SiteMagnet s2 = me.extractMagnetLink(doc2.html());

        assertTrue(s2.getSeeders().equals(1873L));
        assertTrue(s2.getLeechers().equals(141L));
        assertTrue(s2.getUri().equals("magnet:?xt=urn:btih:6705c69dd697753d7d848f0f7b6ccbccf689a453&dn=Geostorm.2017.720p.HC.HDRip.850MB.&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"));

    }


}
