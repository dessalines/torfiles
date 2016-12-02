package com.torshare.webservice;

import ch.qos.logback.classic.Logger;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import org.eclipse.jetty.http.HttpStatus;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Paginator;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static spark.Spark.*;

/**
 * Created by tyler on 11/30/16.
 */
public class Endpoints {

    public static Logger log = (Logger) LoggerFactory.getLogger(Endpoints.class);

    public static void status() {

        get ("/hello", (req, res) -> "hello");

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

        post("/upload", (request, response) -> {

            Path tempDir = Files.createTempDirectory("upload");
            Path tempFile = Files.createTempFile(tempDir, "", "");

            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            log.info(request.raw().toString());

            for (Part e : request.raw().getParts()) {
                log.info(e.getName());
            }

            try (InputStream is = request.raw().getPart("file").getInputStream()) {
                // Use the input stream to create a file
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("temp file: " + tempFile.toAbsolutePath());

            return "File uploaded";
        });

    }




}
