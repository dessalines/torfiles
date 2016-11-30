package com.picard.torrents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class ScanDirectory {

    static final Logger log = LoggerFactory.getLogger(ScanDirectory.class);

    private File calibreDir, torrentsDir, delugeAdds, delugeRemoves;

    public static Set<ScanInfo> start(File calibreDir, File torrentsDir) {
        ScanDirectory sd = new ScanDirectory(calibreDir, torrentsDir);
        return sd.scan();
    }

    private ScanDirectory(File calibreDir, File torrentsDir) {
        this.calibreDir = calibreDir;
        this.torrentsDir = torrentsDir;

        createDelugeFiles();
    }

    private Set<ScanInfo> scan() {

        Tools.folderSize(calibreDir);

        log.info("Scanning directory: " + calibreDir.getAbsolutePath());

        Set<ScanInfo> scanInfos = new LinkedHashSet<>();

        List<File> authorFolders = Arrays.asList(calibreDir.listFiles(File::isDirectory));
        Collections.sort(authorFolders);

        // The main scanning loop
        for (File authorFolder : authorFolders) {

            List<File> bookFolders = Arrays.asList(authorFolder.listFiles(File::isDirectory));
            Collections.sort(bookFolders);

            log.info("Author: " + authorFolder.getName());
            log.info("\tBooks: ");
            for (File bookFolder : bookFolders) {
                log.info("\t\t" + bookFolder.getName());

                // Create a scanInfo from it, check if its a new one added
                ScanInfo si = ScanInfo.create(bookFolder);

                si.setStatus(ScanStatus.Scanning);

                // Add it to the new scan infos
                scanInfos.add(si);

                // Create a torrent for the file, put it in the torrents dir
                si.setStatus(ScanStatus.CreatingTorrent);
                File torrentFile = createAndSaveTorrent(si);

                appendToDelugeFiles(bookFolder, torrentFile);

                si.setStatus(ScanStatus.Done);

                log.info("\n");
            }

        }

        log.info("Done scanning.");

        log.info("Report:");
        log.info(Tools.GSON.toJson(scanInfos));

        return scanInfos;

    }

    private void createDelugeFiles() {
        try {
            this.delugeAdds = new File(torrentsDir, "deluge_adds.sh");
            if (this.delugeAdds.exists()) {
                this.delugeAdds.delete();
            }

            this.delugeAdds.createNewFile();
            this.delugeAdds.setExecutable(true);

            this.delugeRemoves = new File(torrentsDir, "deluge_removes.sh");
            if (this.delugeRemoves.exists()) {
                this.delugeRemoves.delete();
            }

            this.delugeRemoves.createNewFile();
            this.delugeRemoves.setExecutable(true);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToDelugeFiles(File bookFolder, File torrentFile) {
        try {
            String delugeAddLine = "deluge-console \"add -p " + delugeConsoleReplace(bookFolder.getParentFile().getAbsolutePath()) + " " + delugeConsoleReplace(torrentFile.getAbsolutePath()) + "\"\n";
            String delugeRemoveLine = "deluge-console rm \"" + torrentFile.getName() + "\"\n";

            Files.write(delugeAdds.toPath(), delugeAddLine.getBytes(), StandardOpenOption.APPEND);
            Files.write(delugeRemoves.toPath(), delugeRemoveLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String delugeConsoleReplace(String s) {
        String a = s.replaceAll("\\s+", "\\\\ ").replaceAll("'", "\\\\'");
        return a;
    }


    public File createAndSaveTorrent(ScanInfo si) {

        String torrentFileName = constructTorrentFileName(new File(si.getBookDir() + "/metadata.opf"));

        File torrentFile = new File(torrentsDir + "/" + torrentFileName + ".torrent");

        si.setTorrentFile(torrentFile);

        return Tools.createAndSaveTorrent(torrentFile, si.getBookDir());

    }

    /**
     *  Scan the metadata opf to get the name, and identifier
     * @param metaData
     * @return
     */
    public static String constructTorrentFileName(File metaData) {

        String author, title, isbn, publisher, language, tag = "[CLTT]";

        StringBuilder fn = new StringBuilder();

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = null;

            doc = dBuilder.parse(metaData);
            doc.getDocumentElement().normalize();

            title = getXML(doc, "dc:title");

            author = getXML(doc,"dc:creator");

            language = getXML(doc,"dc:language");

            publisher = getXML(doc,"dc:publisher");

            isbn = findISBN(doc);

            fn.append(author + " - " + title + " ");

            if (language != null)
                fn.append("[" + language +"] ");

            if (publisher != null)
                fn.append("[" + publisher +"] ");

            if (isbn != null) {
                fn.append("[ISBN:" + isbn + "] ");
            }

            fn.append(tag);


        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        log.info(fn.toString());
        return fn.toString();
    }

    public static String findISBN(Document doc) {

        String isbn = null;
        try {

            NodeList identifiers = doc.getElementsByTagName("dc:identifier");

            // Loop through the identifiers to see if an isbn is there
            for (int i = 0; i < identifiers.getLength(); i++) {

                Node nNode = identifiers.item(i);

                Element el = (Element) nNode;

                String scheme = el.getAttribute("opf:scheme");

                if (scheme.startsWith("ISBN")) {
                    isbn = el.getTextContent();
                }
            }


        } catch (NullPointerException e) {e.printStackTrace();}

        return isbn;
    }

    public static String getXML(Document doc, String tagName) {

        String ret = null;
        try {
            ret = doc.getElementsByTagName(tagName).item(0).getTextContent();
        } catch(NullPointerException e) {
            log.error("Couldn't find tag: " + tagName);
        }
        return ret;
    }


    /**
     * An enum list of states and messages while scanning
     *
     * @author tyler
     */
    public enum ScanStatus {
        Pending(" "),
        Scanning("Scanning"),
        CreatingTorrent("Creating torrent file"),
        Done("Torrent created");


        private String s;

        ScanStatus(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    public static class ScanInfo {
        private File bookDir;
        private ScanStatus status;
        private File torrentFile;

        public static ScanInfo create(File bookFolder) {
            return new ScanInfo(bookFolder);
        }

        private ScanInfo(File authorFolder) {
            this.bookDir = authorFolder;
            this.status = ScanStatus.Pending;
        }

        public File getBookDir() {
            return bookDir;
        }

        public ScanStatus getStatus() {
            return status;
        }

        public String getStatusString() {
            return status.toString();
        }

        public void setStatus(ScanStatus status) {
            log.info("Status for " + bookDir.getName() + " : " + status.toString());
            this.status = status;
        }

        public void setTorrentFile(File torrentFile) {
            this.torrentFile = torrentFile;
        }

        public File getTorrentFile() {
            return this.torrentFile;
        }


    }


}
