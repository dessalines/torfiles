package com.torfiles.scheduled;

import ch.qos.logback.classic.Logger;
import com.torfiles.tools.Tools;
import org.javalite.activejdbc.DB;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by tyler on 6/15/17.
 */
public class RefreshFastTable implements Job {

    public static Logger log = (Logger) LoggerFactory.getLogger(RefreshFastTable.class);


    private void refreshFastTable() {
        Tools.dbInit();

        log.debug("Refreshing fast table...");
        String sql =
                "create table file_fast_temp as select * from file_view;" +
                "create index idx_file_fast_temp_path_tri_" + UUID.randomUUID().toString().substring(0,5) + " on file_fast_temp using gin (path gin_trgm_ops);" +
                "drop table if exists file_fast;" +
                "alter table file_fast_temp rename to file_fast;";

        try {
            Connection conn = new DB("default").connection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
//            conn.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Tools.dbClose();

        log.debug("Done refreshing fast table.");
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        refreshFastTable();
    }
}