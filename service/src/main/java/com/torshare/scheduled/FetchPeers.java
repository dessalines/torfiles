package com.torshare.scheduled;

import ch.qos.logback.classic.Logger;
import com.torshare.db.Actions;
import com.torshare.db.Tables;
import com.torshare.tools.DataSources;
import com.torshare.tools.Tools;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tyler on 5/30/17.
 */
public class FetchPeers implements Job {


    public static Logger log = (Logger) LoggerFactory.getLogger(FetchPeers.class);


    private Connection connect() {


        // SQLite connection string
        String url = "jdbc:sqlite:" + DataSources.SQLITE_DB.getAbsolutePath();
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public void fetchPeers() {

        log.info("Fetching peers...");
        String sql = "select infohash, count(*) from peers group by infohash order by count(*) desc";

        Map<String, Integer> peerMap = new HashMap<>();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                peerMap.put(rs.getString("infohash"), rs.getInt("count(*)"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Tools.dbInit();
        new DB("default").openTransaction();
        peerMap.entrySet().stream().forEach(e -> Actions.savePeers(e.getKey(), e.getValue()));
        new DB("default").commitTransaction();
        Tools.dbClose();


        log.info("Done fetching peers.");
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        fetchPeers();
    }

}
