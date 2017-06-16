package com.torfiles.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Created by tyler on 11/30/16.
 */
public class Tables {

    @Table("torrent")
    public static class Torrent extends Model {}

    @Table("file")
    public static class File extends Model {}

    @Table("file_fast")
    public static class FileFast extends Model {}

}
