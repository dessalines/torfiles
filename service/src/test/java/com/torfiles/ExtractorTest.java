package com.torfiles;

import com.torfiles.crawler.MagnetExtractor;
import com.torfiles.crawler.SiteMagnet;
import com.torfiles.crawler.sites.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ExtractorTest {

    @Test
    public void skyTorrentsTest() throws Exception {
        MagnetExtractor me = new SkyTorrentsExtractor();

        Document doc = Jsoup.connect("https://www.skytorrents.in/info/9d9b0a063b9dd4aad72dfb6e62617e343ab024f8/The-Shape-of-Water-2017-DVDScr-XVID-AC3-HQ-Hive-CM8-EtMovies/?l=en-us").get();

        SiteMagnet s = me.extractMagnetLink(doc.html());

        assertEquals(s.getSeeders().longValue(), 6838);
        assertEquals(s.getLeechers().longValue(), 2808);
        assertEquals(s.getUri(), "magnet:?xt=urn:btih:9d9b0a063b9dd4aad72dfb6e62617e343ab024f8&dn=The.Shape.of.Water.2017.DVDScr.XVID.AC3.HQ.Hive-CM8%5bEtMovies%5d&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969&tr=udp%3A%2F%2Feddie4.nl%3A6969&tr=udp%3A%2F%2Ftracker.pirateparty.gr%3A6969&tr=udp%3A%2F%2Fopentrackr.org%3A1337&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337");

        Document doc2 = Jsoup.connect("https://www.skytorrents.in/info/c602f490e13474fa580bbbc56e868977a6ffdfa5/Grand-Theft-Auto-IV-Razor1911/").get();

        SiteMagnet s2 = me.extractMagnetLink(doc2.html());

        assertEquals(s2.getSeeders().longValue(), 1L);
        assertEquals(s2.getLeechers().longValue(), 9L);
        assertEquals(s2.getUri(), "magnet:?xt=urn:btih:c602f490e13474fa580bbbc56e868977a6ffdfa5&dn=Grand%20Theft%20Auto%20IV-Razor1911&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969&tr=udp%3A%2F%2Feddie4.nl%3A6969&tr=udp%3A%2F%2Ftracker.pirateparty.gr%3A6969&tr=udp%3A%2F%2Fopentrackr.org%3A1337&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337");


    }

    @Test
    public void thePirateBayTest() throws Exception {
        MagnetExtractor me = new ThePirateBayExtractor();

        Document doc = Jsoup.connect("https://thepiratebay.org/torrent/18346837/Star_Ocean_-_The_Second_Story_(USA)").get();

        SiteMagnet s = me.extractMagnetLink(doc.html());

        assertEquals(s.getSeeders().longValue(), 1L);
        assertEquals(s.getLeechers().longValue(), 0L);
        assertEquals(s.getUri(), "magnet:?xt=urn:btih:f2467748444d5f312921878a2f8b5081221c2054&dn=Star+Ocean+-+The+Second+Story+%28USA%29&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969");

        Document doc2 = Jsoup.connect("https://thepiratebay.org/torrent/19107645/Geostorm.2017.720p.HC.HDRip.850MB").get();

        SiteMagnet s2 = me.extractMagnetLink(doc2.html());

        assertEquals(s2.getSeeders().longValue(), 1657);
        assertEquals(s2.getLeechers().longValue(), 108L);
        assertEquals(s2.getUri(), "magnet:?xt=urn:btih:6705c69dd697753d7d848f0f7b6ccbccf689a453&dn=Geostorm.2017.720p.HC.HDRip.850MB.&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969");

    }

    @Test
    public void _1337XTest() throws Exception {
        MagnetExtractor me = new _1337XExtractor();

        Document doc = Jsoup.connect("https://1337x.to/torrent/2755454/Padmaavat-2018-Padmavati-HIndi-Cam-700mb-TodayPk/").get();

        SiteMagnet s = me.extractMagnetLink(doc.html());

        assertEquals(s.getSeeders().longValue(), 1270L);
        assertEquals(s.getLeechers().longValue(), 489L);
        assertEquals(s.getUri(), "magnet:?xt=urn:btih:8FE4B851EB4B67F72D3EF88C03C3CB7B65EAD0DD&dn=Padmaavat+%282018%29+%5B+Padmavati+%5D+HIndi+Cam+700mb+-+TodayPk&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969%2Fannounce&tr=udp%3A%2F%2F9.rarbg.com%3A2710%2Fannounce&tr=udp%3A%2F%2Fp4p.arenabg.com%3A1337&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Ftracker.internetwarriors.net%3A1337&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969%2Fannounce&tr=udp%3A%2F%2Fcoppersurfer.tk%3A6969%2Fannounce");

    }

//    @Test
//    public void IDopeTest() throws Exception {
//        MagnetExtractor me = new IDopeExtractor();
//
//        Document doc = Jsoup.connect("https://idope.se/torrent/hentai/9f7509e2c8b5e169cb5a9bfce19f9a553431c389/").get();
//
//        SiteMagnet s = me.extractMagnetLink(doc.html());
//
//        assertEquals(s.getSeeders().longValue(), 340L);
//        assertEquals(s.getLeechers().longValue(), 0L);
//        assertEquals(s.getUri(), "magnet:?xt=urn:btih:fcaccfbba672ae10e4cc1c4328e001b96eab23b7");
//
//    }


}
