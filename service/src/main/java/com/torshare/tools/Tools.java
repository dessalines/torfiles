package com.torshare.tools;

import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.swig.*;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Tools {

	static final Logger log = LoggerFactory.getLogger(Tools.class);

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

	public static String readFile(File file) {
		return readFile(file.getAbsolutePath());
	}

	public static void writeFile(String text, String path) {
		try {
			java.nio.file.Files.write(Paths.get(path), text.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String text, File filePath) {
		writeFile(text, filePath.getAbsolutePath());
	}


	public static Long folderSize(File directory) {
		long length = 0;

		Collection<File> files = FileUtils.listFiles(directory, null, true);
		for (File file : files) {
			length += file.length();
		}
		log.info("# of books: " + files.size());

		return length;

	}


	public static File createAndSaveTorrent(File torrentFile, File inputFileOrDir) {

		file_storage fs = new file_storage();

		// Add the file
		libtorrent.add_files(fs, inputFileOrDir.getAbsolutePath());

		create_torrent t = new create_torrent(fs);


		// Add trackers in tiers
		for (URI announce : ANNOUNCE_LIST()) {
			t.add_tracker(new string_view(announce.toASCIIString()));
		}

		t.set_priv(false);
		t.set_creator(System.getProperty("user.name"));

		error_code ec = new error_code();


		// reads the files and calculates the hashes
		libtorrent.set_piece_hashes(t, inputFileOrDir.getParent(), ec);

		if (ec.value() != 0) {
			log.info(ec.message());
		}

		// Get the bencode and write the file
		Entry entry =  new Entry(t.generate());

		Map<String, Entry> entryMap = entry.dictionary();
		Entry entryFromUpdatedMap = Entry.fromMap(entryMap);
		final byte[] bencode = entryFromUpdatedMap.bencode();

		try {
			FileOutputStream fos;

			fos = new FileOutputStream(torrentFile);

			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(bencode);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			log.error("Couldn't write file");
			e.printStackTrace();
		}

		return torrentFile;
	}


//	public static void extractResources() {
//
//		try {
//
//			File currentJar = new File(Tools.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
//
//			File tmpResourcesFolder = Files.createTempDirectory("asdf").toFile();
//
//			DataSources.SOURCE_CODE_HOME = tmpResourcesFolder;
//
//			log.debug("Copying resources to temp libs folder for torrent library");
//
//			// Unzip it and rename it
//			Tools.unzip(currentJar, tmpResourcesFolder);
//
//
//		} catch(URISyntaxException | IOException e) {
//			e.printStackTrace();
//		}
//	}

	public static void unzip(File zipfile, File directory) {
		try {
			ZipFile zfile = new ZipFile(zipfile);
			Enumeration<? extends ZipEntry> entries = zfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(directory, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					InputStream in = zfile.getInputStream(entry);
					try {
						copy(in, file);
					} finally {
						in.close();
					}
				}
			}

			zfile.close();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

//	public static void deleteResourcesOnShutdown() {
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//
//			@Override
//			public void run() {
//				try {
//					FileUtils.deleteDirectory(DataSources.SOURCE_CODE_HOME);
//					log.debug("Temp libs folder deleted");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

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
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String cOrderBy : orderBy) {
			String[] split = cOrderBy.split("-");
			sb.append(sep);
			sb.append(split[0] + " " + split[1]);
			sep = ",";
		}

		return sb.toString();
	}
}

