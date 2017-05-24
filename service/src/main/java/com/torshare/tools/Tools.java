package com.torshare.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.swig.*;
import com.torshare.db.Tables;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
		} finally  {
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
            return "%";
        }
		String[] words = nameQuery.split("\\s+");

		StringBuilder sb = new StringBuilder();
		for (String cWord : words) {
			sb.append("%" + cWord + "%");
		}

		return sb.toString();
	}

	public static String buildOrderBy(String[] orderBy) {

		if (orderBy == null) {
			return "peers desc nulls last";
		}

		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String cOrderBy : orderBy) {
			String[] split = cOrderBy.split("-");
			sb.append(sep);
			sb.append(split[0] + " " + split[1]);
			sep = ",";
		}


		return sb.toString() + " nulls last";
	}

	public static void recursiveDeleteOnShutdownHook(final Path path) {
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Runnable() {
					@Override
					public void run() {
						try {
							Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult visitFile(Path file,
																 @SuppressWarnings("unused") BasicFileAttributes attrs)
										throws IOException {
									Files.delete(file);
									return FileVisitResult.CONTINUE;
								}
								@Override
								public FileVisitResult postVisitDirectory(Path dir, IOException e)
										throws IOException {
									if (e == null) {
										Files.delete(dir);
										return FileVisitResult.CONTINUE;
									}
									// directory iteration failed
									throw e;
								}
							});
						} catch (IOException e) {
							throw new RuntimeException("Failed to delete "+path, e);
						}
					}}));
	}

	public static String torrentsToCsv(LazyList<Tables.Torrent> torrents) {
		StringBuilder sb = new StringBuilder();
		for (Tables.Torrent t : torrents) {
			sb.append(t.getString("info_hash") + ",");
			sb.append(t.getString("name") + "\n");
		}


		return sb.toString();
	}


}

