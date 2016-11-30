package com.picard.torrents;

import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.swig.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Tools {

	static final Logger log = LoggerFactory.getLogger(Tools.class);

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final SimpleDateFormat SDF = new SimpleDateFormat("YYYY");


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
		for (URI announce : DataSources.ANNOUNCE_LIST()) {
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


	public static void extractResources() {

		try {

			File currentJar = new File(Tools.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			File tmpResourcesFolder = Files.createTempDirectory("asdf").toFile();

			DataSources.SOURCE_CODE_HOME = tmpResourcesFolder;

			log.debug("Copying resources to temp libs folder for torrent library");

			// Unzip it and rename it
			Tools.unzip(currentJar, tmpResourcesFolder);


		} catch(URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

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

	public static void deleteResourcesOnShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					FileUtils.deleteDirectory(DataSources.SOURCE_CODE_HOME);
					log.debug("Temp libs folder deleted");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}

