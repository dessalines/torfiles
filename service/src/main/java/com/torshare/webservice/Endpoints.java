package com.torshare.webservice;

import ch.qos.logback.classic.Logger;
import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import com.torshare.torrent.LibtorrentEngine;
import com.torshare.types.TorrentDetail;
import org.eclipse.jetty.http.HttpStatus;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Paginator;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static spark.Spark.*;

/**
 * Created by tyler on 11/30/16.
 */
public class Endpoints {

    public static Logger log = (Logger) LoggerFactory.getLogger(Endpoints.class);

    public static void status() {

        get("/hello", (req, res) -> "hello");

        get("/version", (req, res) -> {
            return "{\"version\":\"" + DataSources.PROPERTIES.getProperty("version") + "\"}";
        });

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Credentials", "true");
            res.header("Access-Control-Allow-Headers", "Origin, content-type,X-Requested-With");
            res.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
            Tools.dbInit();
        });

        after((req, res) -> {
            res.header("Content-Encoding", "gzip");
            Tools.dbClose();
        });

    }

    public static void exceptions() {

        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(e.getMessage());
            Tools.dbClose();
        });
    }

    public static void search() {

        get("/search", (req, res) -> {

            String nameParam = req.queryParams("q");
            String nameTokens = Tools.tokenizeNameQuery(nameParam);

            String limitParam = req.queryParams("limit");
            Integer limit = (limitParam != null) ? Integer.valueOf(limitParam) : 25;

            String pageParam = req.queryParams("page");
            Integer page = (pageParam != null) ? Integer.valueOf(pageParam) : 1;

            String[] orderByParam = req.queryParamsValues("orderBy");
            String orderBy = Tools.buildOrderBy(orderByParam);

            Paginator p = new Paginator(Tables.Torrent.class,
                    limit,
                    "name ilike ?",
                    nameTokens)
                    .orderBy(orderBy);

            LazyList<Tables.Torrent> torrents = p.getPage(page);

            return Tools.wrapPagedResults(torrents.toJson(false),
                    p.getCount(),
                    page);

        });
    }

    public static void upload() {

        options("/upload", (req, res) -> "OKAY");

        post("/upload", (req, res) -> {

            File tempFile = File.createTempFile("temp_file", ".torrent");
            tempFile.deleteOnExit();

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            try (InputStream is = req.raw().getPart("file").getInputStream()) {
                // Use the input stream to create a file
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            log.info(tempFile.getAbsolutePath());

            TorrentInfo ti = new TorrentInfo(tempFile);

            Actions.saveTorrentInfo(ti);

            LibtorrentEngine.INSTANCE.addTorrent(ti);

            // Return the infoHash if it was successful
            return ti.infoHash().toString();
        });

        post("/upload_magnet_links", (req, res) -> {
            log.info(req.body());

            LibtorrentEngine lte = LibtorrentEngine.INSTANCE;
            String lines[] = req.body().split("\\r?\\n");

            Integer torrentsAdded = 0;

            for (int i = 0; i < lines.length; i++) {
                byte[] data = lte.fetchMagnetURI(lines[i]);
                if (data != null) {
                    TorrentInfo ti = TorrentInfo.bdecode(data);
                    Actions.saveTorrentInfo(ti);
                    lte.addTorrent(ti);
                    torrentsAdded++;
                }
            }

            return "{\"message\":\"" + torrentsAdded + " Torrents Added\"}";

        });

    }

    public static void detail() {
        get("/torrent_detail/:info_hash", (req, res) -> {
            String infoHash = req.params(":info_hash");
            Tables.Torrent torrent = Tables.Torrent.findFirst("info_hash = ?", infoHash);

            TorrentInfo ti = TorrentInfo.bdecode(torrent.getBytes("bencode"));

            TorrentDetail td = TorrentDetail.create(
                    ti,
                    torrent.getInteger("seeders"),
                    torrent.getInteger("leechers"));

            return td.json();

        });

    }

    public static void download() {

        get("/torrent_download/:info_hash", (req, res) -> {

            String infoHash = req.params(":info_hash").split(".torrent")[0];
            Tables.Torrent torrent = Tables.Torrent.findFirst("info_hash = ?", infoHash);

            HttpServletResponse raw = res.raw();
            raw.getOutputStream().write(torrent.getBytes("bencode"));
            raw.getOutputStream().flush();
            raw.getOutputStream().close();

            return res.raw();
        });
    }

    public static void export() {

        get("/torshare.pgdump", (req, res) -> {

            File file = File.createTempFile("torshare_dump", ".pgdump");
            file.deleteOnExit();

            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "pg_dump torshare");
            pb.redirectOutput(file);
            final Process process = pb.start();
            process.waitFor();

            HttpServletResponse raw = res.raw();
            raw.getOutputStream().write(Files.readAllBytes(file.toPath()));
            raw.getOutputStream().flush();
            raw.getOutputStream().close();

            return res.raw();
        });

        get("/torshare.json", (req, res) -> {
            LazyList<Tables.Torrent> torrents = Tables.Torrent.findAll();
            return torrents.toJson(false);
        });

    }





}
