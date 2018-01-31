package com.torfiles.crawler.sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RarBGExtractor implements MagnetExtractor {

    public SiteMagnet extractMagnetLink(String html) {
        Document doc = Jsoup.parse(html);

        String magnetUri = doc.selectFirst("table.lista:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > a:nth-child(3)").attr("href");

        String peerStr = doc.selectFirst("table.lista:nth-child(2) > tbody:nth-child(1) > tr:nth-child(28) > td:nth-child(2)").text();
        String peerSplit[] = peerStr.split(" ");

        Long seeders = Long.parseLong(peerSplit[2]);
        Long leechers = Long.parseLong(peerSplit[6]);

        SiteMagnet siteMagnet = new SiteMagnet(magnetUri, seeders, leechers);

        return siteMagnet;

    }
}
