package com.torfiles.scheduled;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;


/**
 * Created by tyler on 5/30/17.
 */

public class ScheduledJobs {
    public static void start() {
        // Another
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            JobDetail refreshViewJob = newJob(RefreshFastTable.class)
                    .build();

            // Trigger the job to run now, and then repeat every x minutes
            Trigger refreshViewTrigger = newTrigger()
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(4)
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(refreshViewJob, refreshViewTrigger);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}
