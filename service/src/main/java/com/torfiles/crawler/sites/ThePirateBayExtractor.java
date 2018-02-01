package com.torfiles.crawler.sites;

import com.torfiles.crawler.MagnetExtractor;
import com.torfiles.crawler.SiteMagnet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ThePirateBayExtractor implements MagnetExtractor {

    public SiteMagnet extractMagnetLink(String html) {

        Document doc = Jsoup.parse(html);

        String magnetUri = doc.selectFirst("div.download:nth-child(1) > a:nth-child(1)").attr("href");

        Element detailsE = doc.getElementById("details");
        try {

            Long seeders = Long.parseLong(detailsE.text().split("Seeders: ")[1].split(" Leechers")[0]);
            Long leechers = Long.parseLong(detailsE.text().split("Leechers: ")[1].split(" ")[0]);

            SiteMagnet siteMagnet = new SiteMagnet(magnetUri, seeders, leechers);

            return siteMagnet;
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println(detailsE.text());
        }

        return null;

    }

}
