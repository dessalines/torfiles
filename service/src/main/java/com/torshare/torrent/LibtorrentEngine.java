package com.torshare.torrent;

import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.*;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import org.apache.commons.io.FileUtils;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


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
