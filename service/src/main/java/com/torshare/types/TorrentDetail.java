package com.torshare.types;

import com.frostwire.jlibtorrent.AnnounceEntry;
import com.frostwire.jlibtorrent.FileStorage;
import com.frostwire.jlibtorrent.TorrentInfo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyler on 12/4/16.
 */
public class TorrentDetail implements JSONWriter {
    private String name, creator, magnetLink, comment, infoHash;
    private Timestamp creationDate;
    private List<String> trackers, files;
    private Integer numFiles, seeders, peers;
    private Long sizeBytes;


    private TorrentDetail(TorrentInfo ti, Integer seeders, Integer peers) {
        this.name = ti.name();
        this.infoHash = ti.infoHash().toString();
        this.creator = ti.creator();
        this.creationDate = new Timestamp(ti.creationDate());
        this.magnetLink = ti.makeMagnetUri();
        this.comment = ti.comment();
        this.numFiles = ti.numFiles();
        this.sizeBytes = ti.totalSize();
        this.seeders = seeders;
        this.peers = peers;

        this.trackers = getTrackers(ti.trackers());

        this.files = filePaths(ti.files());
    }

    public static TorrentDetail create(TorrentInfo ti, Integer seeders, Integer peers) {
        return new TorrentDetail(ti, seeders, peers);
    }

    private static List<String> getTrackers(List<AnnounceEntry> entries) {
        List<String> trackers = new ArrayList<>();
        for (AnnounceEntry e : entries) {
            trackers.add(e.url());
        }

        return trackers;
    }

    private static List<String> filePaths(FileStorage fs) {

        List<String> files = new ArrayList<>();

        for (int i = 0; i < fs.numFiles(); i++) {
            files.add(fs.filePath(i));
        }


        return files;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public String getMagnetLink() {
        return magnetLink;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public List<String> getTrackers() {
        return trackers;
    }

    public List<String> getFiles() {
        return files;
    }

    public Integer getNumFiles() {
        return numFiles;
    }

    public Integer getSeeders() {
        return seeders;
    }

    public Integer getPeers() {
        return peers;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public String getInfoHash() {
        return infoHash;
    }
}
