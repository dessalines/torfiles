package com.torfiles.crawler.sites;

import ch.qos.logback.classic.Logger;
import com.torfiles.crawler.MagnetExtractor;
import com.torfiles.crawler.SiteMagnet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.LoggerFactory;

public class SkyTorrentsExtractor implements MagnetExtractor {

    public static Logger log = (Logger) LoggerFactory.getLogger(SkyTorrentsExtractor.class);

    public SiteMagnet extractMagnetLink(String html) {

        Document doc = Jsoup.parse(html);

        String magnetUri = doc.selectFirst("div.is-6:nth-child(1) > a:nth-child(3)").attr("href");

        Element e = doc.selectFirst("div.is-6:nth-child(1)");

        Long seeders = Long.parseLong(e.text().split("Seeds : ")[1].split(" Peers")[0]);
        Long leechers = Long.parseLong(e.text().split("Peers : ")[1].split(" Size")[0]);

        SiteMagnet siteMagnet = new SiteMagnet(magnetUri, seeders, leechers);

        return siteMagnet;

    }
}
