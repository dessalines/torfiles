package com.torfiles.crawler.sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class _1337XExtractor implements MagnetExtractor {

    public SiteMagnet extractMagnetLink(String html) {
        Document doc = Jsoup.parse(html);

        String magnetUri = doc.selectFirst(".download-links-dontblock > li:nth-child(1) > a:nth-child(1)").attr("href");
        Long seeders = Long.parseLong(doc.selectFirst(".seeds").text());
        Long leechers = Long.parseLong(doc.selectFirst(".leeches").text());

        SiteMagnet siteMagnet = new SiteMagnet(magnetUri, seeders, leechers);

        return siteMagnet;

    }
}
