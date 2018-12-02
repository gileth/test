// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.task.schedule;

import org.quartz.JobExecutionException;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.Job;

public class Task implements Job
{
    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(new DateTime().toLocalDateTime());
    }
}
