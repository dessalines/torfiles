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

/**
 * Created by tyler on 6/15/17.
 */
public class RefreshView implements Job {

    public static Logger log = (Logger) LoggerFactory.getLogger(RefreshView.class);


    private void refreshView() {
        Tools.dbInit();

        log.debug("Refreshing materialized view...");
        String sql = "refresh materialized view concurrently file_view";
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
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        refreshView();
    }
}
