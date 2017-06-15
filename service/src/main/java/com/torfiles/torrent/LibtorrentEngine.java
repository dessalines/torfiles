package com.torfiles.torrent;

import com.frostwire.jlibtorrent.*;
import com.torfiles.tools.DataSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by tyler on 12/2/16.
 */
public enum LibtorrentEngine {

    INSTANCE;

    private Logger log = LoggerFactory.getLogger(LibtorrentEngine.class);

    LibtorrentEngine() {

        System.setProperty("jlibtorrent.jni.path", DataSources.LIBTORRENT_PATH);
        log.info("Starting up libtorrent with version: " + LibTorrent.version());

    }

}
