package com.torfiles.crawler.sites;

import com.frostwire.jlibtorrent.TorrentInfo;
import com.torfiles.db.Actions;
import com.torfiles.tools.Tools;
import com.torfiles.torrent.LibtorrentEngine;

public interface MagnetExtractor {

    SiteMagnet extractMagnetLink(String html);

    default void fetchAndSaveMagnets(String html) {

        SiteMagnet siteMagnet = extractMagnetLink(html);

        TorrentInfo ti = LibtorrentEngine.INSTANCE.fetchMagnet(siteMagnet.getUri());
        Tools.dbInit();
        Actions.saveTorrentInfo(ti, siteMagnet.getSeeders(), siteMagnet.getLeechers());
        Tools.dbClose();

    }

}
