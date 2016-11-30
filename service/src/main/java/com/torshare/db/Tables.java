package com.torshare.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Created by tyler on 11/30/16.
 */
public class Tables {

    @Table("torrent")
    public static class Torrent extends Model {}

    @Table("torrent_view")
    public static class TorrentView extends Model {}

}
