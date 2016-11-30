package com.picard.torrents;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSources {

    static final Logger log = LoggerFactory.getLogger(DataSources.class);

    public static File SOURCE_CODE_HOME;

    public static final String LIBTORRENT_OS_LIBRARY_PATH() {
        String osName = System.getProperty("os.name").toLowerCase();
        String jvmBits = System.getProperty("sun.arch.data.model");
        log.info("Operating system: " + osName + ", JVM bits: " + jvmBits);

        String ret = null;
        if (osName.contains("linux")) {
            if (jvmBits.equals("32")) {
                ret = SOURCE_CODE_HOME + "/lib/x86/libjlibtorrent.so";
            } else {
                ret = SOURCE_CODE_HOME + "/lib/x86_64/libjlibtorrent.so";
            }
        } else if (osName.contains("windows")) {
            if (jvmBits.equals("32")) {
                ret = SOURCE_CODE_HOME + "/lib/x86/jlibtorrent.dll";
            } else {
                ret = SOURCE_CODE_HOME + "/lib/x86_64/jlibtorrent.dll";
            }
        } else if (osName.contains("mac")) {
            ret = SOURCE_CODE_HOME + "/lib/x86_64/libjlibtorrent.dylib";
        }

        log.info("Using libtorrent @ " + ret);
        return ret;
    }

    public static final List<URI> ANNOUNCE_LIST() {
        List<URI> list = null;
        try {
            list = Arrays.asList(
                    new URI("udp://tracker.coppersurfer.tk:6969/announce"),
                    new URI("udp://tracker.opentrackr.org:1337/announce"),
                    new URI("http://tracker.opentrackr.org:1337/announce"),
                    new URI("udp://zer0day.ch:1337/announce"),
                    new URI("http://explodie.org:6969/announce"),
                    new URI("udp://tracker.leechers-paradise.org:6969/announce"),
                    new URI("udp://explodie.org:6969/announce"));

        } catch (URISyntaxException e) {
        }

        return list;
    }

}
