package com.torfiles.types;

import com.torfiles.db.Tables;
import org.javalite.activejdbc.LazyList;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyler on 12/4/16.
 */
public class TorrentDetail implements JSONWriter {
    private String name, infoHash;
    private Timestamp creationDate;
    private List<FileDetail> files;
    private Long peers;
    private Long sizeBytes;


    private TorrentDetail(String name,
                          String infoHash,
                          Timestamp creationDate,
                          List<FileDetail> files,
                          Long peers,
                          Long sizeBytes) {
        this.name = name;
        this.infoHash = infoHash;
        this.creationDate = creationDate;
        this.sizeBytes = sizeBytes;
        this.peers = peers;
        this.files = files;


    }

    public static TorrentDetail create(
            Tables.Torrent torrent,
            LazyList<Tables.FileView> files) {

        List<FileDetail> fileDetails = new ArrayList<>();
        for (Tables.FileView f : files) {
            fileDetails.add(
                    FileDetail.create(
                            f.getString("path"),
                            f.getLong("size_bytes"),
                            f.getInteger("index_")));
        }

        Long peers = files.get(0).getLong("peers");

        return new TorrentDetail(torrent.getString("name"),
                torrent.getString("info_hash"),
                torrent.getTimestamp("created"),
                fileDetails,
                files.get(0).getLong("peers"),
                torrent.getLong("size_bytes"));
    }

    public String getName() {
        return name;
    }

    public String getInfoHash() {
        return infoHash;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public List<FileDetail> getFiles() {
        return files;
    }

    public Long getPeers() {
        return peers;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }
}
