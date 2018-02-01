package com.torfiles.crawler.sites;

import com.torfiles.crawler.MagnetExtractor;
import com.torfiles.crawler.SiteMagnet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class IDopeExtractor implements MagnetExtractor {

    public SiteMagnet extractMagnetLink(String html) {

        Document doc = Jsoup.parse(html);

        String magnetUri = doc.selectFirst("#playnowurl").attr("href");
        Long seeders = Long.parseLong(doc.selectFirst("#infotagseed").text().split(" ")[1]);
        Long leechers = 0L; // No leechers for IDope

        SiteMagnet siteMagnet = new SiteMagnet(magnetUri, seeders, leechers);

        return siteMagnet;
    }
}
