package com.torfiles.crawler;

import ch.qos.logback.classic.Logger;
import com.torfiles.crawler.sites.*;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.regex.Pattern;


public class SiteCrawler extends WebCrawler {

    public static Logger log = (Logger) LoggerFactory.getLogger(SiteCrawler.class);

    public final static Pattern MAGNET_REGEX = Pattern.compile("magnet:\\?xt=urn:btih:[a-z0-9]{20,50}");

    private final static Pattern FILTERS = Pattern.compile(
            ".*(\\.(css|js|gif|jpg"
                    + "|png|mp3|mp4|zip|gz))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && (href.startsWith("https://thepiratebay.org") &&
                !href.startsWith("https://thepiratebay.org/language") ||
                (href.startsWith("https://www.skytorrents.in") && href.endsWith("l=en-us")) ||
                href.startsWith("https://1337x.to/") ||
                href.startsWith("https://rarbg.to/")
        );
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        log.info("Fetching URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();

            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            MagnetExtractor me;

            if (url.startsWith("https://thepiratebay.org/torrent/")) {
                me = new ThePirateBayExtractor();
                me.fetchAndSaveMagnets(html);
            } else if (url.startsWith("https://www.skytorrents.in/info/")) {
                me = new SkyTorrentsExtractor();
                me.fetchAndSaveMagnets(html);
            } else if (url.startsWith("https://1337x.to/torrent/")) {
                me = new _1337XExtractor();
                me.fetchAndSaveMagnets(html);
            } else if (url.startsWith("https://idope.se/torrent/")) {
                me = new IDopeExtractor();
                me.fetchAndSaveMagnets(html);
            } else if (url.startsWith("https://rarbg.to/torrent/")) {
                me = new RarBGExtractor();
                me.fetchAndSaveMagnets(html);
            }


        }
    }


}
