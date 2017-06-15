package com.torfiles.scheduled;

import ch.qos.logback.classic.Logger;
import com.torfiles.db.Actions;
import com.torfiles.tools.DataSources;
import com.torfiles.tools.Tools;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedHashMap;
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
        String sql = "select infohash, count(*) from peers group by infohash order by infohash asc";

        // Build a peerMap so as not to lock sqlite3 reads
        Map<String, Integer> peerMap = new LinkedHashMap<>();

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
        peerMap.entrySet().forEach(e -> Actions.savePeers(e.getKey(), e.getValue()));
        Tools.dbClose();

        log.info("Done fetching peers.");
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        fetchPeers();
    }

}
