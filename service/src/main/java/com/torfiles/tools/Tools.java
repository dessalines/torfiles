package com.torfiles.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.torfiles.db.Actions;
import com.torfiles.db.Tables;
import com.torfiles.torrent.LibtorrentEngine;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.apache.commons.io.FileUtils;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    static final Logger log = LoggerFactory.getLogger(Tools.class);

    public static ObjectMapper JACKSON = new ObjectMapper();
    public static TypeFactory typeFactory = JACKSON.getTypeFactory();

    public static final SimpleDateFormat SDF = new SimpleDateFormat("YYYY");

    public static final void dbInit() {
        try {
            new DB("default").open("org.postgresql.Driver",
                    DataSources.PROPERTIES.getProperty("jdbc.url"),
                    DataSources.PROPERTIES.getProperty("jdbc.username"),
                    DataSources.PROPERTIES.getProperty("jdbc.password"));
        } catch (DBException e) {
            e.printStackTrace();
            dbClose();
            dbInit();
        }

    }

    public static final void dbClose() {
        new DB("default").close();
    }

    public static Properties loadProperties(String propertiesFileLocation) {

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(propertiesFileLocation);

            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return prop;

    }

    public static final String LIBTORRENT_OS_LIBRARY_PATH() {
        String osName = System.getProperty("os.name").toLowerCase();
        String jvmBits = System.getProperty("sun.arch.data.model");
        log.info("Operating system: " + osName + ", JVM bits: " + jvmBits);

        String ret = null;
        if (osName.contains("linux")) {
            if (jvmBits.equals("32")) {
                ret = DataSources.CODE_DIR + "/lib/x86/libjlibtorrent.so";
            } else {
                ret = DataSources.CODE_DIR + "/lib/x86_64/libjlibtorrent.so";
            }
        } else if (osName.contains("windows")) {
            if (jvmBits.equals("32")) {
                ret = DataSources.CODE_DIR + "/lib/x86/jlibtorrent.dll";
            } else {
                ret = DataSources.CODE_DIR + "/lib/x86_64/jlibtorrent.dll";
            }
        } else if (osName.contains("mac")) {
            ret = DataSources.CODE_DIR + "/lib/x86_64/libjlibtorrent.dylib";
        }

        log.info("Using libtorrent @ " + ret);
        return ret;
    }

    public static final List<URI> ANNOUNCE_LIST() {
        List<URI> list = null;
        try {
            list = Arrays.asList(
                    new URI("udp://tracker.coppersurfer.tk:6969/announce"),
                    new URI("udp://tracker.opentrackr.org:1337/announce"));

        } catch (URISyntaxException e) {
        }

        return list;
    }

    public static String readFile(String path) {
        String s = null;

        byte[] encoded;
        try {
            encoded = java.nio.file.Files.readAllBytes(Paths.get(path));

            s = new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("file : " + path + " doesn't exist.");
        }
        return s;
    }

    public static void runLiquibase() {

        Liquibase liquibase = null;
        Connection c = null;
        try {
            c = DriverManager.getConnection(DataSources.PROPERTIES.getProperty("jdbc.url"),
                    DataSources.PROPERTIES.getProperty("jdbc.username"),
                    DataSources.PROPERTIES.getProperty("jdbc.password"));

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
            log.info(DataSources.CHANGELOG_MASTER);
            liquibase = new Liquibase(DataSources.CHANGELOG_MASTER, new FileSystemResourceAccessor(), database);
            liquibase.update("main");
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.rollback();
                    c.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }

    public static String wrapPagedResults(String json, Long count, Integer page) {
        return "{\"results\":" + json + ",\"count\": " + count + ",\"page\":" + page + "}";
    }

    public static String tokenizeNameQuery(String nameQuery) {
        if (nameQuery == null) {
            return null;
        }

        nameQuery = nameQuery.replaceAll("'", "");

        String[] words = nameQuery.split("\\s+");

        String joined = String.join(" & ", words);

        return joined;
    }

    public static byte[] readFileBytes(File f) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void scanTPBFile() {
        /*
        CREATE TABLE `tpb` (
  `title` text,
  `uid` int(11) NOT NULL DEFAULT '0',
  `size` double NOT NULL DEFAULT '0',
  `categoryP` text,
  `categoryS` text,
  `magnet` text,
  `seeders` int(11) NOT NULL DEFAULT '0',
  `leechers` int(11) NOT NULL DEFAULT '0',
  `uploader` text,
  `files` int(11) DEFAULT NULL,
  `time` int(11) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */

        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (BufferedReader br = Files.newBufferedReader(Paths.get("/home/tyler/Tyhous_HD2/Downloads/TPB Backup - Jan.1.2017.sql"),
                StandardCharsets.UTF_8)) {
            int i = 0;
            for (String line = null; (line = br.readLine()) != null; ) {
                log.info("line number i = " + i++);
                if (line.startsWith("INSERT INTO")) {
                    Insert insert = (Insert) CCJSqlParserUtil.parse(line.replaceAll("\\\\'", ""));
                    for (ExpressionList el : ((MultiExpressionList) insert.getItemsList()).getExprList()) {
                        executor.submit(() -> {
                            List<Expression> es = el.getExpressions();
                            String magnetUri = es.get(5).toString();
                            magnetUri = "magnet:" + magnetUri.substring(1, magnetUri.length() - 1);

                            Long seeders = Long.valueOf(es.get(6).toString());
                            Long leechers = Long.valueOf(es.get(6).toString());

                            TorrentInfo ti = LibtorrentEngine.INSTANCE.fetchMagnet(magnetUri);
                            Tools.dbInit();
                            Actions.saveTorrentInfo(ti, seeders, leechers);
                            Tools.dbClose();
                        });

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

