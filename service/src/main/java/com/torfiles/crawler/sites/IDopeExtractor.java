package com.torfiles.crawler.sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IDopeExtractor implements MagnetExtractor {

    public SiteMagnet extractMagnetLink(String html) {

        Document doc = Jsoup.parse(html);

        String magnetUri = doc.selectFirst("#mangetinfo").attr("href");
        Long seeders = Long.parseLong(doc.selectFirst("div.infotag:nth-child(4) > div:nth-child(2)").text());
        Long leechers = 0L; // No leechers for IDope

        SiteMagnet siteMagnet = new SiteMagnet(magnetUri, seeders, leechers);

        return siteMagnet;
    }
}
