package com.torfiles.scheduled;

import ch.qos.logback.classic.Logger;
import com.torfiles.tools.Tools;
import org.javalite.activejdbc.DB;
import org.quartz.DisallowConcurrentExecution;
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

@DisallowConcurrentExecution
public class RefreshFastTable implements Job {

    public static Logger log = (Logger) LoggerFactory.getLogger(RefreshFastTable.class);


    private void refreshView() {
        Tools.dbInit();

        log.debug("Refreshing view...");
        String sql =
               "refresh materialized view concurrently file_view;";

        try {
            Connection conn = new DB("default").connection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Tools.dbClose();

        log.debug("Done refreshing view.");
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        refreshView();
    }
}
